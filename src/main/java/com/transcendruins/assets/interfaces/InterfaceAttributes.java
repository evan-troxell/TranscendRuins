/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.interfaces;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GenerationType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>InterfaceAttributes</code>: A class which represents the attributes of
 * a <code>InterfaceSchema</code> instance.
 */
public final class InterfaceAttributes extends AssetAttributes {

    public enum ComponentType {

        CONTAINER,

        LABEL;

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a
         * <code>ComponentType</code> enum.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>ComponentType</code>: The parsed component type.
         * @throws LoggedException Thrown if the component type could not be found or
         *                         parsed.
         */
        public static final ComponentType parseComponentType(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> entry = collection.getAsString(key, false, null);

            if (!entry.containsValue()) {

                return null;
            }

            String type = entry.getValue();

            return switch (type) {

            case "container" -> CONTAINER;

            case "label" -> LABEL;

            default -> throw new UnexpectedValueException(entry);
            };
        }
    }

    /**
     * <code>String</code>: The regular expression used to match a floating point
     * value.
     */
    private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

    /**
     * <code>ImmutableMap&lt;String, InterfaceAttributes.StyleSchema&gt;</code>: The
     * styles of this <code>InterfaceAttributes</code> instance.
     */
    private final ImmutableMap<String, StyleSchema> styles;

    /**
     * Compiles this <code>InterfaceAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>InterfaceAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>InterfaceAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>InterfaceAttributes</code> instance is the base attribute
     *               set of a <code>InterfaceAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>InterfaceAttributes</code> instance.
     */
    public InterfaceAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        // The styles should only be defined once.
        if (isBase) {

            HashMap<String, StyleSchema> stylesMap = new HashMap<>();

            TracedEntry<TracedDictionary> stylesEntry = json.getAsDict("styles", true);
            if (stylesEntry.containsValue()) {

                TracedDictionary stylesJson = stylesEntry.getValue();

                for (String key : stylesJson) {

                    TracedEntry<TracedDictionary> styleEntry = stylesJson.getAsDict(key, false);
                    TracedDictionary styleJson = styleEntry.getValue();

                    stylesMap.put(key, new StyleSchema(styleJson));
                }
            }

            styles = new ImmutableMap<>(stylesMap);
        } else {

            styles = null;
        }
    }

    /**
     * <code>InterfaceAttributes.Size</code>: A class representing a dimension
     * within a component.
     */
    public static final class Size {

        /**
         * <code>int</code>: A constant representing a size in pixels.
         */
        private static final int PX = 0;

        /**
         * <code>int</code>: A constant representing a size in percent.
         */
        private static final int PCT = 1;

        /**
         * <code>InterfaceAttributes.Size</code>: A constant representing a size of 0
         * pixels.
         */
        public static final Size NONE = new Size(0, PX);

        /**
         * <code>InterfaceAttributes.Size</code>: A constant representing a size of
         * 100%.
         */
        public static final Size FULL = new Size(100, PCT);

        /**
         * <code>double</code>: The dimension of this
         * <code>InterfaceAttributes.Size</code> instance.
         */
        private final double num;

        /**
         * <code>int</code>: The units of this <code>InterfaceAttributes.Size</code>
         * instance.
         */
        private final int suffix;

        /**
         * Retrieves the size of this <code>InterfaceAttributes.Size</code> instance in
         * pixels.
         * 
         * @param parent The size of the parent, in pixels, to use. This is typically
         *               the width or height of the parent.
         * @param min    <code>double</code>: The minimum size in pixels to return. This
         *               should be greater than or equal to 0.
         * @param max    <code>double</code>: The maximum size in pixels to return. This
         *               should be less than or equal to the <code>parent</code>
         *               parameter.
         * @return <code>double</code>: The resulting size, in pixels.
         */
        public double getSize(double parent, double min, double max) {

            double value = switch (suffix) {

            case PX -> num;

            case PCT -> parent * num / 100;

            default -> 0;
            };
            return Math.clamp(value, min, max);
        }

        /**
         * Creates a new instance of the <code>InterfaceAttributes.Size</code> class.
         * 
         * @param num    <code>double</code>: The dimension of this
         *               <code>InterfaceAttributes.Size</code> instance.
         * @param suffix <code>int</code>: The units of this
         *               <code>InterfaceAttributes.Size</code> instance.
         */
        private Size(double num, int suffix) {

            this.num = num;
            this.suffix = suffix;
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>InterfaceAttributes.Size</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>InterfaceAttributes.Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static Size createSize(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(entry -> { // If the entry is a string, the unit should be processed.

                        // Remove all whitespace.
                        String full = entry.getValue().replaceAll("\\s+", "");

                        // Break the suffix off of the number by removing non-digit chars at the end.
                        String numberPart = full.replaceAll("[^\\d.]+.*$", "");
                        if (!numberPart.matches(NUMBER_PATTERN)) {

                            throw new UnexpectedValueException(entry);
                        }

                        double number = Double.parseDouble(numberPart);
                        if (number < 0) {

                            throw new NumberBoundsException(number, entry);
                        }

                        // Isolate the suffix.
                        String suffixPart = full.substring(numberPart.length());
                        int suffix = switch (suffixPart) {

                        case "px", "" -> PX;

                        case "pct", "%" -> PCT;

                        default -> throw new UnexpectedValueException(entry);
                        };

                        if (suffix == PCT && number > 100) {

                            throw new NumberBoundsException(number, entry);
                        }

                        return new Size(number, suffix);
                    }), collection.doubleCase(entry -> { // If the entry is a number, the unit will be pixels.

                        double number = entry.getValue();

                        if (number < 0) {

                            throw new NumberBoundsException(number, entry);
                        }

                        return new Size(number, PX);
                    }), collection.nullCase(_ -> null) // If the entry is missing, return null.
            ));
        }
    }

    /**
     * <code>InterfaceAttributes.StyleSchema</code>: A class representing the schema
     * of the style of a visual component.
     */
    public static final class StyleSchema {

        /**
         * <code>BackgroundStyle</code>: The background of this
         * <code>InterfaceAttributes.StyleSchema</code> instance.
         */
        private final BackgroundStyle background;

        /**
         * <code>ComponentSideStyle</code>: The top side of this
         * <code>InterfaceAttributes.StyleSchema</code> instance.
         */
        private final ComponentSideStyle top;

        /**
         * <code>ComponentSideStyle</code>: The bottom side of this
         * <code>InterfaceAttributes.StyleSchema</code> instance.
         */
        private final ComponentSideStyle bottom;

        /**
         * <code>ComponentSideStyle</code>: The left side of this
         * <code>InterfaceAttributes.StyleSchema</code> instance.
         */
        private final ComponentSideStyle left;

        /**
         * <code>ComponentSideStyle</code>: The right side of this
         * <code>InterfaceAttributes.StyleSchema</code> instance.
         */
        private final ComponentSideStyle right;

        /**
         * Creates a new instance of the <code>InterfaceAttributes.StyleSchema</code>
         * class.
         * 
         * @param json <code>TracedDictionary</code>: The JSON to process.
         * @throws LoggedException Thrown if an error was raised while processing the
         *                         JSON.
         */
        public StyleSchema(TracedDictionary json) throws LoggedException {

            background = BackgroundStyle.createBackgroundStyle(json, "background");

            // Process the borders.
            BorderStyle border = BorderStyle.createBorderStyle(json, "border");

            BorderStyle borderVertical = BorderStyle.createBorderStyle(json, "borderVertical");

            BorderStyle borderTop = findFirst(BorderStyle.createBorderStyle(json, "borderTop"), borderVertical, border);
            BorderStyle borderBottom = findFirst(BorderStyle.createBorderStyle(json, "borderBottom"), borderVertical,
                    border);

            BorderStyle borderHorizontal = BorderStyle.createBorderStyle(json, "borderHorizontal");

            BorderStyle borderLeft = findFirst(BorderStyle.createBorderStyle(json, "borderLeft"), borderHorizontal,
                    border);
            BorderStyle borderRight = findFirst(BorderStyle.createBorderStyle(json, "borderRight"), borderHorizontal,
                    border);

            // Process the border radii.
            Size borderRadius = Size.createSize(json, "borderRadius");

            Size borderRadiusTop = Size.createSize(json, "borderRadiusTop");
            Size borderRadiusBottom = Size.createSize(json, "borderRadiusBottom");
            Size borderRadiusLeft = Size.createSize(json, "borderRadiusLeft");
            Size borderRadiusRight = Size.createSize(json, "borderRadiusRight");

            Size borderRadiusTopLeft = findFirst(Size.createSize(json, "borderRadiusTopLeft"), borderRadiusTop,
                    borderRadiusLeft, borderRadius);
            Size borderRadiusTopRight = findFirst(Size.createSize(json, "borderRadiusTopRight"), borderRadiusTop,
                    borderRadiusRight, borderRadius);

            Size borderRadiusBottomRight = findFirst(Size.createSize(json, "borderRadiusBottomRight"),
                    borderRadiusBottom, borderRadiusRight, borderRadius);
            Size borderRadiusBottomLeft = findFirst(Size.createSize(json, "borderRadiusBottomLeft"), borderRadiusBottom,
                    borderRadiusLeft, borderRadius);

            // Process the margins.
            Size margin = Size.createSize(json, "margin");

            Size marginVertical = Size.createSize(json, "marginVertical");

            Size marginTop = findFirst(Size.createSize(json, "marginTop"), marginVertical, margin);
            Size marginBottom = findFirst(Size.createSize(json, "marginBottom"), marginVertical, margin);

            Size marginHorizontal = Size.createSize(json, "marginHorizontal");

            Size marginLeft = findFirst(Size.createSize(json, "marginLeft"), marginHorizontal, margin);
            Size marginRight = findFirst(Size.createSize(json, "marginRight"), marginHorizontal, margin);

            // Process the paddings.
            Size padding = Size.createSize(json, "padding");

            Size paddingVertical = Size.createSize(json, "paddingVertical");

            Size paddingTop = findFirst(Size.createSize(json, "paddingTop"), paddingVertical, padding);
            Size paddingBottom = findFirst(Size.createSize(json, "paddingBottom"), paddingVertical, padding);

            Size paddingHorizontal = Size.createSize(json, "paddingHorizontal");

            Size paddingLeft = findFirst(Size.createSize(json, "paddingLeft"), paddingHorizontal, padding);
            Size paddingRight = findFirst(Size.createSize(json, "paddingRight"), paddingHorizontal, padding);

            // Generate the sides.
            top = new ComponentSideStyle(borderTop, borderRadiusTopLeft, marginTop, paddingTop);
            bottom = new ComponentSideStyle(borderBottom, borderRadiusBottomRight, marginBottom, paddingBottom);
            left = new ComponentSideStyle(borderLeft, borderRadiusBottomLeft, marginLeft, paddingLeft);
            right = new ComponentSideStyle(borderRight, borderRadiusTopRight, marginRight, paddingRight);
        }

        /**
         * <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code>: A class
         * representing the style of a background.
         */
        public static final class BackgroundStyle {

            /**
             * <code>Color</code>: The color of this
             * <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code> instance.
             */
            private final Color color;

            /**
             * <code>String</code>: The image of this
             * <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code> instance.
             */
            private final String image;

            /**
             * Creates a new instance of the
             * <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code> class.
             * 
             * @param json <code>TracedDictionary</code>: The JSON to process.
             * @throws LoggedException Thrown if an error was raised while processing the
             *                         JSON.
             */
            private BackgroundStyle(TracedDictionary json) throws LoggedException {

                TracedEntry<Color> colorEntry = json.getAsColor("color", true, null);
                color = colorEntry.getValue();

                TracedEntry<String> imageEntry = json.getAsString("image", true, null);
                image = imageEntry.getValue();
            }

            /**
             * Parses a value from a <code>TracedCollection</code> instance into a new
             * <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code> instance.
             * 
             * @param collection <code>TracedCollection</code>: The collection to parse
             *                   from.
             * @param key        <code>Object</code>: The key to search for.
             * @return <code>InterfaceAttributes.StyleSchema.BackgroundStyle</code>: The
             *         parsed background style.
             * @throws LoggedException Thrown if an error occurs while parsing the
             *                         collection.
             */
            public static BackgroundStyle createBackgroundStyle(TracedCollection collection, Object key)
                    throws LoggedException {

                return collection.get(key, List.of(

                        collection.dictCase(entry -> { // If the entry is a dictionary, process it into a background
                                                       // style.

                            TracedDictionary json = entry.getValue();
                            return new BackgroundStyle(json);
                        }), collection.nullCase(_ -> null) // If the entry is missing, return null.
                ));
            }
        }

        /**
         * <code>InterfaceAttributes.StyleSchema.BorderStyle</code>: A class
         * representing the style of a border.
         */
        public static final class BorderStyle {

            /**
             * <code>InterfaceAttributes.Size</code>: The width of this
             * <code>InterfaceAttributes.StyleSchema.BorderStyle</code> instance.
             */
            private final Size width;

            /**
             * <code>Color</code>: The color of this
             * <code>InterfaceAttributes.StyleSchema.BorderStyle</code> instance.
             */
            private final Color color;

            /**
             * Creates a new instance of the
             * <code>InterfaceAttributes.StyleSchema.BorderStyle</code> class.
             * 
             * @param json <code>TracedDictionary</code>: The JSON to process.
             * @throws LoggedException Thrown if an error was raised while processing the
             *                         JSON.
             */
            private BorderStyle(TracedDictionary json) throws LoggedException {

                width = Size.createSize(json, "size");

                TracedEntry<Color> colorEntry = json.getAsColor("color", true, null);
                color = colorEntry.getValue();
            }

            /**
             * Parses a value from a <code>TracedCollection</code> instance into a new
             * <code>InterfaceAttributes.StyleSchema.BorderStyle</code> instance.
             * 
             * @param collection <code>TracedCollection</code>: The collection to parse
             *                   from.
             * @param key        <code>Object</code>: The key to search for.
             * @return <code>InterfaceAttributes.StyleSchema.BorderStyle</code>: The parsed
             *         border style.
             * @throws LoggedException Thrown if an error occurs while parsing the
             *                         collection.
             */
            public static BorderStyle createBorderStyle(TracedCollection collection, Object key)
                    throws LoggedException {

                return collection.get(key, List.of(

                        collection.dictCase(entry -> { // If the entry is a dictionary, process it into a border style.

                            TracedDictionary json = entry.getValue();
                            return new BorderStyle(json);
                        }), collection.nullCase(_ -> null) // If the entry is missing, return null.
                ));
            }
        }

        /**
         * <code>InterfaceAttributes.StyleSchema.ComponentSideStyle</code>: A record
         * representing the properties of a single side (top, bottom, left, right) of a
         * component.
         * 
         * @param border       <code>BorderStyle</code>: The border of this
         *                     <code>InterfaceAttributes.StyleSchema.ComponentSideStyle</code>
         *                     instance.
         * @param borderRadius <code>InterfaceAttributes.Size</code>: The border radius
         *                     of this
         *                     <code>InterfaceAttributes.StyleSchema.ComponentSideStyle</code>
         *                     instance.
         * @param margin       <code>InterfaceAttributes.Size</code>: The margin of this
         *                     <code>InterfaceAttributes.StyleSchema.ComponentSideStyle</code>
         *                     instance.
         * @param padding      <code>InterfaceAttributes.Size</code>: The padding of
         *                     this
         *                     <code>InterfaceAttributes.StyleSchema.ComponentSideStyle</code>
         *                     instance.
         */
        public static final record ComponentSideStyle(BorderStyle border, Size borderRadius, Size margin,
                Size padding) {
        }
    }

    public ComponentSchema createComponent(TracedDictionary json) throws LoggedException {

        ComponentType type = ComponentType.parseComponentType(json, "type");

        return switch (type) {

        case CONTAINER -> new ContainerComponentSchema(json);

        case LABEL -> new LabelComponentSchema(json);

        default -> null;
        };
    }

    /**
     * <code>InterfaceAttributes.ComponentSchema</code>: A class representing the
     * schema of a visual component.
     */
    public abstract class ComponentSchema {

        private final Size width;

        private final Size height;

        private final Size x;

        private final Size y;

        private final ImmutableList<StyleSchema> style;

        private final ImmutableList<StyleSchema> onHoverStyle;

        private final ImmutableList<StyleSchema> onClickStyle;

        public ComponentSchema(TracedDictionary json) throws LoggedException {

            width = findFirst(Size.createSize(json, "width"), Size.FULL);
            height = findFirst(Size.createSize(json, "height"), Size.FULL);
            x = findFirst(Size.createSize(json, "x"), Size.NONE);
            y = findFirst(Size.createSize(json, "y"), Size.NONE);

            style = createStyle(json, "style");
            onHoverStyle = createStyle(json, "onHoverStyle");
            onClickStyle = createStyle(json, "onClickStyle");
        }

        private ImmutableList<StyleSchema> createStyle(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(entry -> { // If the entry is a string, retrieve the corresponding style.

                        String styleKey = entry.getValue();

                        if (!styles.containsKey(styleKey)) {

                            throw new ReferenceWithoutDefinitionException(entry, "Style");
                        }

                        return new ImmutableList<>(styles.get(styleKey));
                    }),

                    collection.dictCase(entry -> { // If the entry is a dictionary, process it as a style.

                        TracedDictionary json = entry.getValue();
                        return new ImmutableList<>(new StyleSchema(json));
                    }),

                    collection.arrayCase(arrayEntry -> { // If the entry is an array, process each subentry as its own
                                                         // style.

                        ArrayList<StyleSchema> styleList = new ArrayList<>();

                        TracedArray array = arrayEntry.getValue();
                        for (int i : array) {

                            StyleSchema styleSchema = array.get(i, List.of(

                                    array.stringCase(entry -> { // If the entry is a string, retrieve the corresponding
                                                                // style.

                                        String styleKey = entry.getValue();

                                        if (!styles.containsKey(styleKey)) {

                                            throw new ReferenceWithoutDefinitionException(entry, "Style");
                                        }

                                        return styles.get(styleKey);
                                    }),

                                    array.dictCase(entry -> { // If the entry is a dictionary, process it as a style.

                                        TracedDictionary json = entry.getValue();
                                        return new StyleSchema(json);
                                    })));

                            styleList.add(styleSchema);
                        }

                        return new ImmutableList<>(styleList);
                    })

            ));
        }
    }

    /**
     * Finds the first level in a provided list which is non-null.
     * 
     * @param <K>    The type of the levels to process.
     * @param levels <code>K...</code>: The levels to process. An example would be
     *               borderRight, borderHorizontal, border.
     * @return <code>K</code>: The first non-null level.
     */
    public static <K> K findFirst(K... levels) {

        for (K level : levels) {

            if (level != null) {

                return level;
            }
        }

        return null;
    }

}
