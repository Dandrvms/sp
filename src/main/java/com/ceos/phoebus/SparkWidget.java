package com.ceos.phoebus;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private WidgetProperty<Boolean> timer;
    private WidgetProperty<Integer> polling;

    public SparkWidget() {
        super(WIDGET_TYPE);
    }

    public WidgetProperty<Boolean> propTimer(){
        return timer;
    }

    public WidgetProperty<Integer> propPolling(){
        return polling;
    }

    private static final Set<String> ALLOWED_PROPS = Set.of(
        "title", "unit", "value", "minValue", "maxValue",
        "decimals", "averagingPeriod",
        "titleColor", "unitColor", "valueColor", "backgroundColor",
        "barColor", "strokeWithGradient", "opacity",
        "threshold", "thresholdColor", "borderWidth", "roundedCorners",
        "gradientStops",
        "chartGridColor", "tickLabelColor", "textColor", "borderColor",
        "descriptionColor", "foregroundColor",
        "textSize", "titleAlignment",
        "fixedYScale", "smoothing", "animated", "shortenNumbers",
        "shadowsEnabled", "textVisible", "valueVisible", "averageVisible"
    );

    @Override
    protected void defineProperties(final List<WidgetProperty<?>> properties) {
        super.defineProperties(properties);

        properties.add(timer = CommonWidgetProperties.propEnabled.createProperty(this, false));
//        properties.add(polling = CommonWidgetProperties.newIntegerPropertyDescriptor(WidgetPropertyCategory.MISC, "polling", "polling rate")
//                .createProperty(this, 1_000_000_000));

        if (tilePropertyMap == null) {
            tilePropertyMap = new LinkedHashMap<>();
        }

        try {
            PropertyDescriptor[] tileProps = PropertyUtils.getPropertyDescriptors(Tile.class);
            for (PropertyDescriptor pd : tileProps) {
                String name = pd.getName();
                if (!ALLOWED_PROPS.contains(name)) continue;
                if (pd.getWriteMethod() == null) continue;

                Class<?> type = pd.getPropertyType();
                if (type == null) continue;

                boolean alreadyDefined = properties.stream().anyMatch(p -> p.getName().equals(name));
                if (alreadyDefined) continue;

                WidgetProperty<?> prop = createTileProperty(name, type);
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

        if ("gradientStops".equals(name)) {
            String defaultVal = "0.0=#00FF00, 0.5=#FFFF00, 1.0=#FF0000";
            return CommonWidgetProperties.newStringPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }

        if (type == String.class) {
            String defaultVal = switch (name) {
                case "title" -> "Title";
                case "unit" -> "units";
                default -> "";
            };
            return CommonWidgetProperties.newStringPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == double.class || type == Double.class) {
            double defaultVal = switch (name) {
                case "maxValue" -> 100.0;
                case "opacity" -> 1.0;
                case "threshold" -> 80.0;
                case "borderWidth" -> 1.0;
                default -> 0.0;
            };
            return CommonWidgetProperties.newDoublePropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == int.class || type == Integer.class) {
            int defaultVal = switch (name) {
                case "averagingPeriod" -> 7;
                case "decimals" -> 1;
                default -> 0;
            };
            return CommonWidgetProperties.newIntegerPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == boolean.class || type == Boolean.class) {
            boolean defaultVal = switch (name) {
                case "strokeWithGradient", "roundedCorners", "animated", "smoothing", "shadowsEnabled" -> true;
                default -> false;
            };
            return CommonWidgetProperties.newBooleanPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultVal);
        }
        if (type == javafx.scene.paint.Color.class) {
            WidgetColor defaultColor = switch (name) {
                case "backgroundColor" -> new WidgetColor(0, 0, 0);
                case "thresholdColor" -> new WidgetColor(255, 0, 0);
                case "chartGridColor" -> new WidgetColor(255, 255, 255);
                default -> new WidgetColor(255, 255, 255);
            };
            return CommonWidgetProperties.newColorPropertyDescriptor(cat, name, name)
                .createProperty(this, defaultColor);
        }
        if (type.isEnum()) {
            Enum<?> defaultVal = enumDefault(name, type);
            WidgetPropertyDescriptor desc = new WidgetPropertyDescriptor(cat, name, name) {
                @Override public WidgetProperty createProperty(Widget w, Object val) {
                    return new EnumWidgetProperty(this, w, (Enum) val);
                }
            };
            return desc.createProperty(this, defaultVal);
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Enum<?> enumDefault(String name, Class<?> type) {
        Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
        if ("titleAlignment".equals(name) && "javafx.scene.text.TextAlignment".equals(type.getName())) {
            return Enum.valueOf(enumClass, "LEFT");
        }
        return enumClass.getEnumConstants()[0];
    }

    public Map<String, WidgetProperty<?>> getTilePropertyMap() {
        if (tilePropertyMap == null) {
            tilePropertyMap = new LinkedHashMap<>();
        }
        return tilePropertyMap;
    }
}
