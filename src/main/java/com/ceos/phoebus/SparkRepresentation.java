package com.ceos.phoebus;

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
        model_widget.propPolling().addUntypedPropertyListener(contentChangedListener);

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
        jfx_node.setPolling(model_widget.propPolling().getValue());

        for (Map.Entry<String, WidgetProperty<?>> entry : model_widget.getTilePropertyMap().entrySet()) {
            String propName = entry.getKey();
            if ("skinType".equals(propName)) continue;
            WidgetProperty<?> prop = entry.getValue();
            Object value = prop.getValue();

            Object tileValue = value;
            if (value instanceof WidgetColor wc) {
                tileValue = JFXUtil.convert(wc);
            }

            try {
                PropertyUtils.setProperty(tile, propName, tileValue);
            } catch (Exception e) {
                System.err.println("Error setting tile property '" + propName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

        WidgetProperty<?> skinTypeProp = model_widget.getTilePropertyMap().get("skinType");
        if (skinTypeProp != null) {
            try {
                PropertyUtils.setProperty(tile, "skinType", skinTypeProp.getValue());
            } catch (Exception e) {
                System.err.println("Error setting tile property 'skinType': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void contentChanged(final WidgetProperty<?> prop, final Object old, Object val) {
        dirtyLook.mark();
        toolkit.scheduleUpdate(this);
    }
}
