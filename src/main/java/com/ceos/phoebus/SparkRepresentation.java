package com.ceos.phoebus;

import com.ceos.sparkline.SparkLine;
import org.csstudio.display.builder.model.DirtyFlag;
import org.csstudio.display.builder.model.UntypedWidgetPropertyListener;
import org.csstudio.display.builder.model.WidgetProperty;
import org.csstudio.display.builder.representation.javafx.widgets.JFXBaseRepresentation;

public class SparkRepresentation extends JFXBaseRepresentation<SparkLine, SparkWidget> {
    private DirtyFlag dirty_look = new DirtyFlag();
    private final UntypedWidgetPropertyListener contentChangedListener = this::contentChanged;

    @Override
    protected SparkLine createJFXNode() throws Exception {
        return new SparkLine();
    }

    @Override
    public void updateChanges() {
        super.updateChanges();

        if(dirty_look.checkAndClear()){
            int width = model_widget.propWidth().getValue();
            int height = model_widget.propHeight().getValue();

            jfx_node.setPrefWidth(width);
            jfx_node.setPrefHeight(height);
        }

    }

    @Override
    public void registerListeners(){
        super.registerListeners();
        model_widget.propWidth().addUntypedPropertyListener(contentChangedListener);
        model_widget.propHeight().addUntypedPropertyListener(contentChangedListener);

    }


    private void contentChanged(final WidgetProperty<?> prop, final Object old, Object val){
        dirty_look.mark();
        toolkit.scheduleUpdate(this);
    }
}
