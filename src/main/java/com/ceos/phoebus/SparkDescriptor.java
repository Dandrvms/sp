package com.ceos.phoebus;

import org.csstudio.display.builder.model.Widget;
import org.csstudio.display.builder.model.WidgetCategory;
import org.csstudio.display.builder.model.WidgetDescriptor;

public class SparkDescriptor extends WidgetDescriptor {

    public SparkDescriptor(){
        super(SparkWidget.WIDGET_TYPE, WidgetCategory.MONITOR, "plot", "", "Spark Line");

    }

    @Override
    public Widget createWidget() {
        return new SparkWidget();
    }
}
