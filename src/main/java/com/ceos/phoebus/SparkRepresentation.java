package com.ceos.phoebus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.ceos.sparkline.SparkLine;

import org.csstudio.display.builder.model.DirtyFlag;
import org.csstudio.display.builder.model.UntypedWidgetPropertyListener;
import org.csstudio.display.builder.model.WidgetProperty;
import org.csstudio.display.builder.representation.javafx.JFXUtil;
import org.csstudio.display.builder.representation.javafx.widgets.JFXBaseRepresentation;
import org.phoebus.ui.color.WidgetColor;

import eu.hansolo.tilesfx.Tile;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

public class SparkRepresentation extends JFXBaseRepresentation<SparkLine, SparkWidget> {
    private DirtyFlag dirtyLook = new DirtyFlag();
    private Tile tile;

    private final UntypedWidgetPropertyListener contentChangedListener = this::contentChanged;

    @Override
    protected SparkLine createJFXNode() throws Exception {
        SparkLine node = new SparkLine();
        tile = node.getTile();
        return node;
    }

    @Override
    public void registerListeners() {
        super.registerListeners();

        model_widget.propWidth().addUntypedPropertyListener(contentChangedListener);
        model_widget.propHeight().addUntypedPropertyListener(contentChangedListener);
        model_widget.propTimer().addUntypedPropertyListener(contentChangedListener);
//        model_widget.propPolling().addUntypedPropertyListener(contentChangedListener);

        for (WidgetProperty<?> prop : model_widget.getTilePropertyMap().values()) {
            prop.addUntypedPropertyListener(contentChangedListener);
        }
    }

    @Override
    public void updateChanges() {
        super.updateChanges();

        if (!dirtyLook.checkAndClear()) return;

        int width = model_widget.propWidth().getValue();
        int height = model_widget.propHeight().getValue();
        jfx_node.setPrefWidth(width);
        jfx_node.setPrefHeight(height);
        jfx_node.startTimer(model_widget.propTimer().getValue());
//        jfx_node.setPolling(model_widget.propPolling().getValue());

        for (Map.Entry<String, WidgetProperty<?>> entry : model_widget.getTilePropertyMap().entrySet()) {
            String propName = entry.getKey();
            WidgetProperty<?> prop = entry.getValue();
            Object value = prop.getValue();

            try {
                if ("gradientStops".equals(propName)) {

//                    tile.setGradientStops(parseGradientStops((String) value));
                } else {
                    Object tileValue = value;
                    if (value instanceof WidgetColor wc) {
                        tileValue = JFXUtil.convert(wc);
                    }
                    PropertyUtils.setProperty(tile, propName, tileValue);
                }
            } catch (Exception e) {
                System.err.println("Error setting tile property '" + propName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

        tile.requestLayout();
    }

//    private static List<Stop> parseGradientStops(String text) {
//        List<Stop> stops = new ArrayList<>();
//        if (text == null || text.isBlank()) return stops;
//        String[] parts = text.split(",");
//        for (String part : parts) {
//            part = part.trim();
//            String[] kv = part.split("=");
//            if (kv.length == 2) {
//                try {
//                    double offset = Double.parseDouble(kv[0].trim());
//                    Color color = Color.web(kv[1].trim());
//                    stops.add(new Stop(offset, color));
//                } catch (Exception ignored) {
//                }
//            }
//        }
//        return stops;
//    }

    private void contentChanged(final WidgetProperty<?> prop, final Object old, Object val) {
        dirtyLook.mark();
        toolkit.scheduleUpdate(this);
    }
}
