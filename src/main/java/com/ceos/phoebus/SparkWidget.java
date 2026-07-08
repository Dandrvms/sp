package com.ceos.phoebus;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import org.csstudio.display.builder.model.Widget;
import org.csstudio.display.builder.model.WidgetProperty;
import org.csstudio.display.builder.model.WidgetPropertyCategory;
import org.csstudio.display.builder.model.WidgetPropertyDescriptor;
import org.csstudio.display.builder.model.properties.CommonWidgetProperties;
import org.csstudio.display.builder.model.properties.EnumWidgetProperty;
import org.csstudio.display.builder.model.widgets.PVWidget;
import org.phoebus.ui.color.WidgetColor;

import eu.hansolo.tilesfx.Tile;

public class SparkWidget extends PVWidget {

    public static final String WIDGET_TYPE = "spark_line";

    private Map<String, WidgetProperty<?>> tilePropertyMap;
    private Map<String, Method> tileSetters;
    private WidgetProperty<Boolean> timer;
    private WidgetProperty<Integer> polling;

    public SparkWidget() {
        super(WIDGET_TYPE);
    }

    public WidgetProperty<Boolean> propTimer(){
        return timer;
    }

//    public WidgetProperty<Integer> propPolling(){
//        return polling;
//    }

    public Map<String, Method> getTileSetters() {
        if (tileSetters == null) {
            tileSetters = new LinkedHashMap<>();
        }
        return tileSetters;
    }

    public Method getTileSetter(String propName) {
        if (tileSetters == null) return null;
        return tileSetters.get(propName);
    }

    @Override
    protected void defineProperties(final List<WidgetProperty<?>> properties) {
        super.defineProperties(properties);

        properties.add(timer = CommonWidgetProperties.propEnabled.createProperty(this, false));
//        properties.add(polling = CommonWidgetProperties.newIntegerPropertyDescriptor(WidgetPropertyCategory.MISC, "polling", "polling rate")
//                .createProperty(this, 1_000_000_000));

        if (tilePropertyMap == null) {
            tilePropertyMap = new LinkedHashMap<>();
        }
        if (tileSetters == null) {
            tileSetters = new LinkedHashMap<>();
        }

        try {
            PropertyDescriptor[] tileProps = PropertyUtils.getPropertyDescriptors(Tile.class);
            for (PropertyDescriptor pd : tileProps) {
                if (pd.getWriteMethod() == null) continue;
                String name = pd.getName();
                Class<?> type = pd.getPropertyType();
                if (type == null || "class".equals(name)) continue;

                // Keep only Tile-specific properties (skip inherited from Region/Control/Node)
                Class<?> declaringClass = pd.getWriteMethod().getDeclaringClass();
                if (declaringClass == null || !declaringClass.getName().startsWith("eu.hansolo.tilesfx")) continue;

                // Skip if parent class already defines this property (e.g. visible, style, width)
                boolean alreadyDefined = properties.stream().anyMatch(p -> p.getName().equals(name));
                if (alreadyDefined) continue;

                // Cache the setter method for direct invocation
                tileSetters.put(name, pd.getWriteMethod());

                WidgetProperty<?> prop = null;
                if(!"skinType".equals(name))  prop = createTileProperty(name, type);
                if (prop != null) {
                    properties.add(prop);
                    tilePropertyMap.put(name, prop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private WidgetProperty<?> createTileProperty(String name, Class<?> type) {
        WidgetPropertyCategory cat = WidgetPropertyCategory.DISPLAY;

        if (type == String.class) {
            String defaultVal = switch (name) {
                case "title" -> "SparkLine";
                case "unit" -> "°C";
                default -> "";
            };
            return CommonWidgetProperties.newStringPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == double.class || type == Double.class) {
            double defaultVal = switch (name) {
                case "maxValue" -> 100.0;
                case "opacity" -> 1.0;
                default -> 0.0;
            };
            return CommonWidgetProperties.newDoublePropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == int.class || type == Integer.class) {
            int defaultVal = switch (name) {
                case "minorTickCount" -> 5;
                case "averagingPeriod" -> 7;
                default -> 0;
            };
            return CommonWidgetProperties.newIntegerPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == long.class || type == Long.class) {
            return CommonWidgetProperties.newLongPropertyDescriptor(cat, name, name)
                .createProperty(this, 0L);
        }
        if (type == boolean.class || type == Boolean.class) {
            boolean defaultVal = "strokeWithGradient".equals(name);
            return CommonWidgetProperties.newBooleanPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == javafx.scene.paint.Color.class) {
            return CommonWidgetProperties.newColorPropertyDescriptor(cat, name, name)
                .createProperty(this, new WidgetColor(255, 255, 255));
        }
        if (type.isEnum()) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
            Enum<?> defaultVal = "skinType".equals(name)
                ? Tile.SkinType.SPARK_LINE
                : enumClass.getEnumConstants()[0];
            WidgetPropertyDescriptor desc = new WidgetPropertyDescriptor(cat, name, name) {
                @Override
                public WidgetProperty createProperty(Widget w, Object val) {
                    return new EnumWidgetProperty(this, w, (Enum) val);
                }
            };
            return desc.createProperty(this, defaultVal);
        }
        return null;
    }

    public Map<String, WidgetProperty<?>> getTilePropertyMap() {
        if (tilePropertyMap == null) {
            tilePropertyMap = new LinkedHashMap<>();
        }
        return tilePropertyMap;
    }
}
