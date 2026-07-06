package com.ceos.phoebus;

import org.csstudio.display.builder.model.WidgetDescriptor;
import org.csstudio.display.builder.model.spi.WidgetsService;

import java.util.Collection;
import java.util.List;

public class SparkWidgetsService implements WidgetsService {
    @Override
    public Collection<WidgetDescriptor> getWidgetDescriptors() {
        return List.of(new SparkDescriptor());
    }
}
