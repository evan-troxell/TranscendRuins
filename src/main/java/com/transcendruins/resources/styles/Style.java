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

package com.transcendruins.resources.styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Style</code>: A record representing the schema of the style of a visual
 * component.
 */
public final record Style(Size x, Size y, Size width, Size height, Size minWidth, Size minHeight,
        BackgroundStyle background, BorderStyle borderTop, BorderStyle borderBottom, BorderStyle borderLeft,
        BorderStyle borderRight, SizeDimensions rTL, SizeDimensions rTR, SizeDimensions rBL, SizeDimensions rBR,
        Size marginTop, Size marginBottom, Size marginLeft, Size marginRight, Size paddingTop, Size paddingBottom,
        Size paddingLeft, Size paddingRight, Integer fontStyle, Integer fontWeight, Size fontSize, Size lineHeight,
        String fontFamily, Color color, Size gap, Direction listDirection, Boolean eventPropagation) {

    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null);

    public static final Size AUTO = new Size(parent -> parent);

    /**
     * Creates a new instance of the <code>Style</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection to process.
     * @param key        <code>Object</code>: The key to retrieve.
     * @throws LoggedException Thrown if an error was raised while processing the
     *                         JSON.
     */
    public static Style createStyle(TracedCollection collection, Object key) throws LoggedException {

        Size x = null;
        Size y = null;

        Size width = null;
        Size height = null;
        Size minWidth = null;
        Size minHeight = null;

        BackgroundStyle background = null;

        BorderStyle borderTop = null;
        BorderStyle borderBottom = null;
        BorderStyle borderLeft = null;
        BorderStyle borderRight = null;

        SizeDimensions rTL = null;
        SizeDimensions rTR = null;
        SizeDimensions rBL = null;
        SizeDimensions rBR = null;

        Size marginTop = null;
        Size marginBottom = null;
        Size marginLeft = null;
        Size marginRight = null;

        Size paddingTop = null;
        Size paddingBottom = null;
        Size paddingLeft = null;
        Size paddingRight = null;

        Integer fontStyle = null;
        Integer fontWeight = null;
        Size fontSize = null;
        Size lineHeight = null;
        String fontFamily = null;
        Color color = null;

        WhiteSpaceStyle whiteSpace = null; // If text extends too far, should it wrap?
        OverflowWrapStyle overflowWrap = null; // If text needs to wrap, where should it break?
        TextOverflowStyle textOverflow = null; // If a line is still off the screen, what should the line end with?

        OverflowStyle overflowX = null; // When do you scroll X?
        OverflowStyle overflowY = null; // When do you scroll Y?

        Size gap = null;
        Direction listDirection = null;

        Boolean eventPropagation = null;

        TracedEntry<TracedDictionary> entry = collection.getAsDict(key, true);
        if (entry.containsValue()) {

            TracedDictionary json = entry.getValue();

            // Iterate through each key in the JSON.
            for (String property : json) {

                switch (property) {

                // Process the coordinates.
                case "x" -> x = Size.createSize(json, property);
                case "y" -> y = Size.createSize(json, property);

                // Process the size.
                case "width" -> width = createAutoSize(json, property);
                case "height" -> height = createAutoSize(json, property);
                case "minWidth" -> minWidth = Size.createSize(json, property);
                case "minHeight" -> minHeight = Size.createSize(json, property);

                // Process the background.
                case "background" -> background = BackgroundStyle.createBackgroundStyle(json, property);

                // Process the border.
                case "border" -> borderTop = borderBottom = borderLeft = borderRight = BorderStyle
                        .createBorderStyle(json, property);
                case "borderVertical" -> borderTop = borderBottom = BorderStyle.createBorderStyle(json, property);
                case "borderHorizontal" -> borderLeft = borderRight = BorderStyle.createBorderStyle(json, property);
                case "borderTop" -> borderTop = BorderStyle.createBorderStyle(json, property);
                case "borderBottom" -> borderBottom = BorderStyle.createBorderStyle(json, property);
                case "borderLeft" -> borderLeft = BorderStyle.createBorderStyle(json, property);
                case "borderRight" -> borderRight = BorderStyle.createBorderStyle(json, property);

                // Process the border radius.
                case "borderRadius" -> rTL = rTR = rBL = rBR = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusTop" -> rTL = rTR = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusBottom" -> rBL = rBR = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusLeft" -> rTL = rBL = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusRight" -> rTR = rBR = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusTopLeft" -> rTL = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusTopRight" -> rTR = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusBottomLeft" -> rBL = SizeDimensions.createSizeDimensions(json, property);
                case "borderRadiusBottomRightv" -> rBR = SizeDimensions.createSizeDimensions(json, property);

                // Process the margin.
                case "margin" -> marginTop = marginBottom = marginLeft = marginRight = Size.createSize(json, property);
                case "marginVertical" -> marginTop = marginBottom = Size.createSize(json, property);
                case "marginHorizontal" -> marginLeft = marginRight = Size.createSize(json, property);
                case "marginTop" -> marginTop = Size.createSize(json, property);
                case "marginBottom" -> marginBottom = Size.createSize(json, property);
                case "marginLeft" -> marginLeft = Size.createSize(json, property);
                case "marginRight" -> marginRight = Size.createSize(json, property);

                // Process the padding.
                case "padding" -> paddingTop = paddingBottom = paddingLeft = paddingRight = Size.createSize(json,
                        property);
                case "paddingVertical" -> paddingTop = paddingBottom = Size.createSize(json, property);
                case "paddingHorizontal" -> paddingLeft = paddingRight = Size.createSize(json, property);
                case "paddingTop" -> paddingTop = Size.createSize(json, property);
                case "paddingBottom" -> paddingBottom = Size.createSize(json, property);
                case "paddingLeft" -> paddingLeft = Size.createSize(json, property);
                case "paddingRight" -> paddingRight = Size.createSize(json, property);

                // Process the font properties.
                case "font" -> {

                    TracedEntry<TracedDictionary> fontEntry = json.getAsDict(key, false);
                    TracedDictionary fontJson = fontEntry.getValue();

                    // If the font dictionary has a style, apply it.
                    if (fontJson.containsKey("style")) {

                        fontStyle = createFontStyle(fontJson, "style");
                    }

                    // If the font dictionary has a weight, apply it.
                    if (fontJson.containsKey("weight")) {

                        fontWeight = createFontWeight(fontJson, "weight");
                    }

                    // If the font dictionary has a size, apply it.
                    if (fontJson.containsKey("size")) {

                        fontSize = Size.createSize(fontJson, "size");
                    }

                    // If the font dictionary has a height, apply it.
                    if (fontJson.containsKey("height")) {

                        lineHeight = createLineHeight(fontJson, "height");
                    }

                    // If the font dictionary has a family, apply it.
                    if (fontJson.containsKey("family")) {

                        fontFamily = createFontFamily(fontJson, "family");
                    }
                }
                case "fontStyle" -> fontStyle = createFontStyle(json, key);
                case "fontWeight" -> fontWeight = createFontWeight(json, key);
                case "fontSize" -> fontSize = Size.createSize(json, key);
                case "lineHeight" -> lineHeight = createLineHeight(json, key);
                case "fontFamily" -> fontFamily = createFontFamily(json, key);
                case "color" -> {

                    TracedEntry<Color> colorEntry = json.getAsColor(key, false, null);
                    color = colorEntry.getValue();
                }

                // Process the text wrapping and overflow.
                case "whiteSpace" -> whiteSpace = WhiteSpaceStyle.createWhiteSpace(json, key); // normal (default),
                                                                                               // nowrap
                case "overflowWrap" -> overflowWrap = OverflowWrapStyle.createOverflowWrap(json, key); // normal
                                                                                                       // (default),
                                                                                                       // breakWord
                case "textOverflow" -> textOverflow = TextOverflowStyle.createTextOverflow(json, key); // clip
                                                                                                       // (default),
                                                                                                       // ellipses

                // Process the overflow.
                case "overflow" -> overflowX = overflowY = OverflowStyle.createOverflow(json, key); // hidden (default),
                                                                                                    // auto, scroll
                case "overflowX" -> overflowX = OverflowStyle.createOverflow(json, key);
                case "overflowY" -> overflowY = OverflowStyle.createOverflow(json, key);

                // Process the list element gap.
                case "gap" -> gap = Size.createSize(json, property);

                // Process the list direction.
                case "listDirection" -> listDirection = Direction.createDirection(json, property);

                // Process the event propagation behavior.
                case "eventPropagation" -> {

                    TracedEntry<Boolean> eventPropagationEntry = json.getAsBoolean(key, false, null);
                    eventPropagation = eventPropagationEntry.getValue();
                }
                }
            }
        }

        return new Style(x, y, width, height, minWidth, minHeight, background, borderTop, borderBottom, borderLeft,
                borderRight, rTL, rTR, rBL, rBR, marginTop, marginBottom, marginLeft, marginRight, paddingTop,
                paddingBottom, paddingLeft, paddingRight, fontStyle, fontWeight, fontSize, lineHeight, fontFamily,
                color, gap, listDirection, eventPropagation);
    }

    public static Style createStyle(List<Style> styles, Style parent) {

        // The latter elements in styles should be caught first.
        styles = styles.reversed();

        return new Style(

                // All sizing properties should be independent.
                parseVal(styles, Style::x, Size.NONE), parseVal(styles, Style::y, Size.NONE),
                parseVal(styles, Style::width, Size.FULL), parseVal(styles, Style::height, AUTO),
                parseVal(styles, Style::minWidth, Size.NONE), parseVal(styles, Style::minHeight, Size.NONE),
                parseVal(styles, Style::background, BackgroundStyle.DEFAULT),
                parseVal(styles, Style::borderTop, BorderStyle.DEFAULT),
                parseVal(styles, Style::borderBottom, BorderStyle.DEFAULT),
                parseVal(styles, Style::borderLeft, BorderStyle.DEFAULT),
                parseVal(styles, Style::borderRight, BorderStyle.DEFAULT),
                parseVal(styles, Style::rTL, SizeDimensions.NONE), parseVal(styles, Style::rTR, SizeDimensions.NONE),
                parseVal(styles, Style::rBL, SizeDimensions.NONE), parseVal(styles, Style::rBR, SizeDimensions.NONE),
                parseVal(styles, Style::marginTop, Size.NONE), parseVal(styles, Style::marginBottom, Size.NONE),
                parseVal(styles, Style::marginLeft, Size.NONE), parseVal(styles, Style::marginRight, Size.NONE),
                parseVal(styles, Style::paddingTop, Size.NONE), parseVal(styles, Style::paddingBottom, Size.NONE),
                parseVal(styles, Style::paddingLeft, Size.NONE), parseVal(styles, Style::paddingRight, Size.NONE),

                // All font properties should inherit parent properties.
                parseVal(styles, Style::fontStyle, parent, Font.PLAIN),
                parseVal(styles, Style::fontWeight, parent, Font.PLAIN),
                parseVal(styles, Style::fontSize, parent, Size.FULL),
                parseVal(styles, Style::lineHeight, parent, new Size(value -> value * 1.2)),
                parseVal(styles, Style::fontFamily, parent, null), parseVal(styles, Style::color, parent, Color.BLACK),

                // All listing properties should be independent.
                parseVal(styles, Style::gap, Size.NONE), parseVal(styles, Style::listDirection, Direction.VERTICAL),

                parseVal(styles, Style::eventPropagation, null));
    }

    /**
     * Finds the first defined property from a list of styles.
     * 
     * @param <K>    The property type.
     * @param styles <code>List&lt;Style&gt;</code>: The list of styles to search
     *               through.
     * @param getter <code>Function&lt;Style, K&gt;</code>: The retrieval function
     *               to use.
     * @param ifNull <code>K</code>: The value to return if one was not found.
     * @return <code>K</code>: The first retrieved property, or <code>ifNull</code>
     *         if the property was not found.
     */
    private static <K> K parseVal(List<Style> styles, Function<Style, K> getter, K ifNull) {

        for (Style style : styles) {

            K val = getter.apply(style);
            if (val != null) {

                return val;
            }
        }

        return ifNull;
    }

    /**
     * Finds the first defined property from a list of styles.
     * 
     * @param <K>    The property type.
     * @param styles <code>List&lt;Style&gt;</code>: The list of styles to search
     *               through.
     * @param getter <code>Function&lt;Style, K&gt;</code>: The retrieval function
     *               to use.
     * @param parent <code>Style</code>: The parent whose value to return if one was
     *               not found.
     * @param ifNull <code>K</code>: The value to return if one was not found in the
     *               parent.
     * @return <code>K</code>: The first retrieved property, or the property in
     *         <code>parent</code> if the property was not found.
     */
    public static <K> K parseVal(List<Style> styles, Function<Style, K> getter, Style parent, K ifNull) {

        // Use the parent values as the ifnull if it exists.
        K sub = getter.apply(parent);
        if (sub == null) {

            sub = ifNull;
        }
        return parseVal(styles, getter, sub);
    }

    /**
     * <code>Style.Size</code>: A class representing a dimension within a component.
     */
    public static final class Size {

        /**
         * <code>String</code>: The regular expression used to match a floating point
         * value.
         */
        private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

        /**
         * <code>Style.Size</code>: A constant representing a size of 0 pixels.
         */
        public static final Size NONE = new Size(_ -> 0.0);

        /**
         * <code>Style.Size</code>: A constant representing a size of 100%.
         */
        public static final Size FULL = new Size(parent -> parent);

        /**
         * <code>Style.Size</code>: A constant representing a size of 1 pixel.
         */
        public static final Size PX_1 = new Size(_ -> 1.0);

        /**
         * <code>Function&lt;Integer, Double&gt;</code>: The operator used to calculate
         * the size of this <code>Style.Size</code> instance.
         */
        private final Function<Double, Double> operator;

        /**
         * Retrieves the size of this <code>Style.Size</code> instance in pixels.
         * 
         * @param parent <code>double</code>: The size of the parent, in pixels, to use.
         *               This is typically the width or height of the parent.
         * @param min    <code>int</code>: The minimum size in pixels to return. This
         *               should be greater than or equal to 0.
         * @return <code>double</code>: The resulting size, in pixels.
         */
        public int getSize(double parent, int min) {

            int value = operator.apply(parent).intValue();

            return Math.max(min, value);
        }

        /**
         * Creates a new instance of the <code>Style.Size</code> class.
         * 
         * @param operator <code>Function&lt;Double, Double&gt;</code>: The operator
         *                 used to calculate the size of this <code>Style.Size</code>
         *                 instance.
         */
        private Size(Function<Double, Double> operator) {

            this.operator = operator;
        }

        /**
         * Parses a string-formatted size into a new <code>Style.Size</code> instance.
         * 
         * @param entry <code>TracedEntry&lt;String&gt;</code>: The entry to parse.
         * @return <code>Style.Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the entry.
         */
        public static Size createStringSize(TracedEntry<String> entry) throws LoggedException {

            String full = entry.getValue().replaceAll(" ", "");
            if (full.charAt(0) != '+' && full.charAt(0) != '-') {

                full = '+' + full;
            }

            // Find each operator in the string.
            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < full.length(); i++) {

                char c = full.charAt(i);
                if (c == '+' || c == '-') {

                    indices.add(i);
                }
            }

            indices.add(full.length());

            ArrayList<Function<Double, Double>> add = new ArrayList<>();
            ArrayList<Function<Double, Double>> minus = new ArrayList<>();

            // Construct the operators for each part of the string.
            for (int i = 0; i < indices.size() - 1; i++) {

                int start = indices.get(i);
                int end = indices.get(i + 1);

                String part = full.substring(start, end);
                Function<Double, Double> operator = createOperator(entry, part);
                if (part.charAt(0) == '+') {

                    add.add(operator);
                } else {

                    minus.add(operator);
                }
            }

            // Create a size which sums over the operators.
            return new Size(parent -> add.stream().mapToDouble(f -> f.apply(parent)).sum()
                    - minus.stream().mapToDouble(f -> f.apply(parent)).sum());
        }

        /**
         * Creates a new operator from a string-formatted size.
         * 
         * @param entry <code>TracedEntry&lt;String&gt;</code>: The entry to parse.
         * @param full  <code>String</code>: The full string to parse.
         * @return <code>Function&lt;Double, Double&gt;</code>: The operator created
         *         from the string.
         * @throws LoggedException Thrown if an error occurs while parsing the entry.
         */
        private static Function<Double, Double> createOperator(TracedEntry<String> entry, String full)
                throws LoggedException {

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
            return switch (suffixPart) {

            case "px", "" -> (_ -> number);

            case "pct", "%" -> (parent -> number * parent / 100.0);

            default -> throw new UnexpectedValueException(entry);
            };
        }

        /**
         * Parses a double into a new <code>Style.Size</code> instance.
         * 
         * @param entry <code>TracedEntry&lt;Double&gt;</code>: The entry to parse.
         * @return <code>Style.Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the entry.
         */
        public static Size createDoubleSize(TracedEntry<Double> entry) throws LoggedException {

            double number = entry.getValue();
            if (number < 0) {

                throw new NumberBoundsException(number, entry);
            }

            return new Size(_ -> number);
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Style.Size</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>Style.Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static Size createSize(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(Size::createStringSize), collection.doubleCase(Size::createDoubleSize)));
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Style.Size</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @param ifNull     <code>Style.Size</code>: The value to return if a size
         *                   could not be created.
         * @return <code>Style.Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static Size createSize(TracedCollection collection, Object key, Size ifNull) throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(Size::createStringSize), collection.doubleCase(Size::createDoubleSize),
                    collection.nullCase(_ -> ifNull)));
        }
    }

    private static Size createAutoSize(TracedCollection collection, Object key) throws LoggedException {

        return collection.get(key, List.of(collection.stringCase(entry -> {

            String size = entry.getValue();
            if (size.equals("auto")) {

                return AUTO;
            }

            return Size.createSize(collection, key);
        }), collection.defaultCase(_ -> Size.createSize(collection, key))));
    }

    /**
     * <code>TextureSize</code>: A class representing the size configuration of a
     * texture.
     */
    public static abstract class TextureSize {

        /**
         * <code>TextureSize</code>: A texture size representing constant, 1:1 scaling.
         */
        public static final TextureSize AUTO = new TextureSize() {

            @Override
            public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return textureWidth;
            }

            @Override
            public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return textureHeight;
            }
        };

        /**
         * <code>TextureSize</code>: A texture size representing scaling to contain the
         * entire graphic in the parent's bounds.
         */
        public static final TextureSize CONTAIN = new TextureSize() {

            @Override
            public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return Math.min(textureWidth * parentWidth / textureWidth, textureWidth * parentHeight / textureHeight);
            }

            @Override
            public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return Math.min(textureHeight * parentWidth / textureWidth,
                        textureHeight * parentHeight / textureHeight);
            }
        };

        /**
         * <code>TextureSize</code>: A texture size representing scaling to fill the
         * parent's bounds.
         */
        public static final TextureSize COVER = new TextureSize() {

            @Override
            public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return Math.max(textureWidth * parentWidth / textureWidth, textureWidth * parentHeight / textureHeight);
            }

            @Override
            public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return Math.max(textureHeight * parentWidth / textureWidth,
                        textureHeight * parentHeight / textureHeight);
            }
        };

        /**
         * Creates a new instance of the <code>TextureSize</code> class sized based on
         * the variable dimensions of a parent component.
         * 
         * @param width  <code>Size</code>: The width to calculate using.
         * @param height <code>Size</code>: The height to calculate using.
         * @return <code>TextureSize</code>: The generated texture size.
         */
        public static TextureSize createSize(Size width, Size height) {

            return new TextureSize() {

                @Override
                public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                    return width.getSize(parentWidth, 0);
                }

                @Override
                public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                    return height.getSize(parentHeight, 0);
                }
            };
        }

        /**
         * Creates a new instance of the <code>TextureSize</code> class of constant
         * proportions sized based on the variable dimensions of a parent component.
         * 
         * @param width <code>Size</code>: The width to calculate using.
         * @return <code>TextureSize</code>: The generated texture size.
         */
        public static TextureSize createSize(Size width) {

            return new TextureSize() {

                @Override
                public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                    return width.getSize(parentWidth, 0);
                }

                @Override
                public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                    return (width.getSize(parentWidth, 0) * textureHeight / textureWidth);
                }
            };
        }

        /**
         * Parses a collection into a new instance of the <code>Style</code> class.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve from the
         *                   collection.
         * @return <code>TextureSize</code>: The generated texture size.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static TextureSize createSize(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    // Parse into a width-height texture size.
                    collection.arrayCase(sizeEntry -> {

                        TracedArray sizeJson = sizeEntry.getValue();
                        if (sizeJson.size() != 2) {

                            throw new CollectionSizeException(sizeEntry, sizeJson);
                        }

                        return TextureSize.createSize(Size.createSize(sizeJson, 0, Size.FULL),
                                Size.createSize(sizeJson, 1, Size.FULL));
                    }),

                    // Parse into a width-height texture size.
                    collection.dictCase(sizeEntry -> {

                        TracedDictionary sizeJson = sizeEntry.getValue();

                        return TextureSize.createSize(Size.createSize(sizeJson, "width", Size.FULL),
                                Size.createSize(sizeJson, "height", Size.FULL));
                    }), collection.stringCase(sizeEntry -> {

                        String sizeString = sizeEntry.getValue();
                        return switch (sizeString) {

                        case "contain" -> TextureSize.CONTAIN;

                        case "cover" -> TextureSize.COVER;

                        case "auto" -> TextureSize.AUTO;

                        default -> TextureSize.createSize(Size.createStringSize(sizeEntry));
                        };
                    }), collection.doubleCase(sizeEntry -> TextureSize.createSize(Size.createDoubleSize(sizeEntry))),
                    collection.nullCase(_ -> TextureSize.AUTO)));
        };

        /**
         * Retrieves the width of this <code>TextureSize</code> instance in pixels.
         * 
         * @param textureWidth  <code>int</code>: The width, in pixels, of the texture.
         * @param textureHeight <code>int</code>: The height, in pixels, of the texture.
         * @param parentWidth   <code>int</code>: The width, in pixels, of the parent
         *                      component.
         * @param parentHeight  <code>int</code>: The height, in pixels, of the parent
         *                      component.
         * @return <code>int</code>: The generated width.
         */
        public abstract int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight);

        /**
         * Retrieves the height of this <code>TextureSize</code> instance in pixels.
         * 
         * @param textureWidth  <code>int</code>: The width, in pixels, of the texture.
         * @param textureHeight <code>int</code>: The height, in pixels, of the texture.
         * @param parentWidth   <code>int</code>: The width, in pixels, of the parent
         *                      component.
         * @param parentHeight  <code>int</code>: The height, in pixels, of the parent
         *                      component.
         * @return <code>int</code>: The generated height.
         */
        public abstract int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight);

    }

    /**
     * <code>Style.BackgroundStyle</code>: A record representing the style of a
     * background.
     */
    public static final record BackgroundStyle(Color color, String texture, TextureSize size) {

        public static final BackgroundStyle DEFAULT = new BackgroundStyle(new Color(0, 0, 0, 0), null,
                TextureSize.AUTO);

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Style.BackgroundStyle</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>Style.BackgroundStyle</code>: The parsed background style.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static BackgroundStyle createBackgroundStyle(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<TracedDictionary> entry = collection.getAsDict(key, true);
            if (!entry.containsValue()) {

                return null;
            }

            TracedDictionary json = entry.getValue();

            TracedEntry<Color> colorEntry = json.getAsColor("color", true, null);
            Color color = colorEntry.getValue();

            TracedEntry<String> textureEntry = json.getAsString("texture", true, null);
            String texture = textureEntry.getValue();

            TextureSize size = TextureSize.createSize(json, "size");
            return new BackgroundStyle(color, texture, size);
        }
    }

    /**
     * <code>Style.BorderStyle</code>: A record representing the style of a border.
     */
    public static final record BorderStyle(Size width, Color color) {

        public static final BorderStyle DEFAULT = new BorderStyle(Size.PX_1, null);

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Style.BorderStyle</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @param ifNull     <code>BorderStyle</code>: The value to return if a border
         *                   style could not be created.
         * @return <code>Style.BorderStyle</code>: The parsed border style.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static BorderStyle createBorderStyle(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();

                        Size width = Size.createSize(json, "size", Size.PX_1);

                        TracedEntry<Color> colorEntry = json.getAsColor("color", true, Color.BLACK);
                        Color color = colorEntry.getValue();

                        return new BorderStyle(width, color);
                    })));
        }
    }

    /**
     * <code>SizeDimensions</code>: A record representing a set of horizontal and
     * vertical dimensions.
     */
    public static final record SizeDimensions(Size width, Size height) {

        /**
         * <code>SizeDimensions</code>: A size dimension of <code>0px</code> by
         * <code>0px</code>.
         */
        public static final SizeDimensions NONE = new SizeDimensions(Size.NONE, Size.NONE);

        /**
         * Creates a new instance of the <code>SizeDimensions</code> class.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>SizeDimensions</code>: The generated size dimensions.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static SizeDimensions createSizeDimensions(TracedCollection collection, Object key)
                throws LoggedException {

            return collection.get(key, List.of(

                    // Parse into a width-height size dimensions.
                    collection.arrayCase(entry -> {

                        TracedArray json = entry.getValue();
                        if (json.size() != 2) {

                            throw new CollectionSizeException(entry, json);
                        }

                        Size width = Size.createSize(json, 0, Size.NONE);
                        Size height = Size.createSize(json, 1, Size.NONE);

                        return new SizeDimensions(width, height);
                    }),

                    // Parse into a width-height size dimensions.
                    collection.dictCase(entry -> {

                        TracedDictionary json = entry.getValue();
                        Size width = Size.createSize(json, "width", Size.NONE);
                        Size height = Size.createSize(json, "height", Size.NONE);

                        return new SizeDimensions(width, height);
                    }),

                    // Parse into a square dimension.
                    collection.stringCase(entry -> {

                        Size size = Size.createStringSize(entry);
                        return new SizeDimensions(size, size);
                    }),

                    // Parse into a square dimension.
                    collection.doubleCase(entry -> {

                        Size size = Size.createDoubleSize(entry);
                        return new SizeDimensions(size, size);
                    })));
        }
    }

    public static final int createFontStyle(TracedCollection json, Object key) throws LoggedException {

        TracedEntry<String> fontStyleEntry = json.getAsString(key, false, null);
        String fontStyle = fontStyleEntry.getValue();
        return switch (fontStyle) {

        case "normal" -> Font.PLAIN;
        case "italic" -> Font.ITALIC;
        default -> throw new UnexpectedValueException(fontStyleEntry);
        };
    }

    public static final int createFontWeight(TracedCollection json, Object key) throws LoggedException {

        TracedEntry<String> fontWeightEntry = json.getAsString(key, false, null);
        String fontWeight = fontWeightEntry.getValue();
        return switch (fontWeight) {

        case "normal" -> Font.PLAIN;
        case "bold" -> Font.BOLD;
        default -> throw new UnexpectedValueException(fontWeightEntry);
        };
    }

    public static final Size createLineHeight(TracedCollection json, Object key) throws LoggedException {

        return json.get(key, List.of(

                // A line width of x should really be x*100% of the text size.
                json.doubleCase(entry -> {

                    double factor = entry.getValue();
                    return new Size(parent -> parent * factor);
                }),

                // Any other size should work normally.
                json.defaultCase(_ -> Size.createSize(json, key))));
    }

    public static final String createFontFamily(TracedCollection json, Object key) throws LoggedException {

        return json.get(key, List.of(json.stringCase(fontFamilyEntry -> {

            String fontFamily = fontFamilyEntry.getValue();
            return getFirstAvailableFont(List.of(fontFamily));
        }), json.arrayCase(fontFamiliesEntry -> {

            TracedArray fontFamiliesArray = fontFamiliesEntry.getValue();

            ArrayList<String> fontFamilies = new ArrayList<>();
            for (int i : fontFamiliesArray) {

                TracedEntry<String> fontFamilyEntry = fontFamiliesArray.getAsString(i, false, null);
                String fontFamily = fontFamilyEntry.getValue();

                fontFamilies.add(fontFamily);
            }

            return getFirstAvailableFont(fontFamilies);
        })));
    }

    private static String getFirstAvailableFont(List<String> fontFamilies) {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        for (String family : fontFamilies) {

            for (String available : availableFonts) {

                if (available.equalsIgnoreCase(family)) {

                    // Return the first font.
                    return available;
                }
            }
        }

        // If all fonts are exhausted, return null.
        return null;
    }

    /**
     * <code>Direction</code>: An enum class representing a vertical or horizontal
     * direction.
     */
    public static enum Direction {

        /**
         * <code>Direction</code>: An enum constant representing a horizontal direction.
         */
        HORIZONTAL,

        /**
         * <code>Direction</code>: An enum constant representing a vertical direction.
         */
        VERTICAL;

        /**
         * Parses a collection into a horizontal or vertical direction.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting direction.
         */
        public static Direction createDirection(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(collection.stringCase(entry -> {

                String value = entry.getValue();
                return switch (value) {

                case "horizontal" -> Direction.HORIZONTAL;
                case "vertical" -> Direction.VERTICAL;
                default -> throw new UnexpectedValueException(entry);
                };
            })));
        }
    }
}