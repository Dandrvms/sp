package com.ceos.phoebus;

import java.lang.reflect.Method;
import java.util.Map;

import com.ceos.sparkline.SparkLine;

import org.csstudio.display.builder.model.DirtyFlag;
import org.csstudio.display.builder.model.UntypedWidgetPropertyListener;
import org.csstudio.display.builder.model.WidgetProperty;
import org.csstudio.display.builder.representation.javafx.JFXUtil;
import org.csstudio.display.builder.representation.javafx.widgets.JFXBaseRepresentation;
import org.phoebus.ui.color.WidgetColor;

import eu.hansolo.tilesfx.Tile;

public class SparkRepresentation extends JFXBaseRepresentation<SparkLine, SparkWidget> {
    private DirtyFlag dirtyLook = new DirtyFlag();
    private Tile tile;
    private boolean initialTilePropsApplied = false;

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

        for (WidgetProperty<?> prop : model_widget.getTilePropertyMap().values()) {
            prop.addUntypedPropertyListener(this::tilePropertyChanged);
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

        if (!initialTilePropsApplied) {
            applyAllTileProperties();
            initialTilePropsApplied = true;
        }
    }

    private void applyAllTileProperties() {
        for (Map.Entry<String, WidgetProperty<?>> entry : model_widget.getTilePropertyMap().entrySet()) {
            String propName = entry.getKey();
            if ("skinType".equals(propName)) continue;
            WidgetProperty<?> prop = entry.getValue();
            applyTileProperty(propName, prop.getValue());
        }

        WidgetProperty<?> skinTypeProp = model_widget.getTilePropertyMap().get("skinType");
        if (skinTypeProp != null) {
            applyTileProperty("skinType", skinTypeProp.getValue());
        }

        tile.requestLayout();
    }

    private void applyTileProperty(String propName, Object value) {
        Object tileValue = value;
        if (value instanceof WidgetColor wc) {
            tileValue = JFXUtil.convert(wc);
        }

        Method setter = model_widget.getTileSetter(propName);
        if (setter != null) {
            try {
                setter.invoke(tile, tileValue);
            } catch (Exception e) {
                System.err.println("Error setting tile property '" + propName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void tilePropertyChanged(final WidgetProperty<?> prop, final Object old, Object val) {
        String propName = prop.getName();

        // skinType recreates the skin; re-apply all properties
        if ("skinType".equals(propName)) {
            applyAllTileProperties();
            return;
        }

        applyTileProperty(propName, val);
        tile.requestLayout();
    }

    private void contentChanged(final WidgetProperty<?> prop, final Object old, Object val) {
        dirtyLook.mark();
        toolkit.scheduleUpdate(this);
    }
}
