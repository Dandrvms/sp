package com.ceos.phoebus;

import org.csstudio.display.builder.model.WidgetProperty;
import org.csstudio.display.builder.model.widgets.PVWidget;

import java.util.List;

public class SparkWidget extends PVWidget {

    public static final String WIDGET_TYPE = "spark_line";

    public SparkWidget() {
        super(WIDGET_TYPE);
    }

    protected void defineProperties(final List<WidgetProperty<?>> properties){
        super.defineProperties(properties);
    }
}
