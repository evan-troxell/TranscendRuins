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
import java.util.List;
import java.util.function.Function;

import com.transcendruins.assets.interfaces.ComponentSize;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>Style</code>: A class representing the schema of the style of a visual
 * component.
 */
public final class Style {

    /**
     * <code>BackgroundStyle</code>: The background of this <code>Style</code>
     * instance.
     */
    private final BackgroundStyle background;

    /**
     * Retrieves the background style of this <code>Style</code> instance.
     * 
     * @return <code>BackgroundStyle</code>: The <code>background</code> field of
     *         this <code>Style</code> instance.
     */
    public BackgroundStyle getBackground() {

        return background;
    }

    /**
     * <code>ComponentSideStyle</code>: The top side of this <code>Style</code>
     * instance.
     */
    private final ComponentSideStyle top;

    /**
     * Retrieves the top side of this <code>Style</code> instance.
     * 
     * @return <code>ComponentSideStyle</code>: The <code>top</code> field of this
     *         <code>Style</code> instance.
     */
    public ComponentSideStyle getTop() {

        return top;
    }

    /**
     * <code>ComponentSideStyle</code>: The bottom side of this <code>Style</code>
     * instance.
     */
    private final ComponentSideStyle bottom;

    /**
     * Retrieves the bottom side of this <code>Style</code> instance.
     * 
     * @return <code>ComponentSideStyle</code>: The <code>bottom</code> field of
     *         this <code>Style</code> instance.
     */
    public ComponentSideStyle getBottom() {

        return bottom;
    }

    /**
     * <code>ComponentSideStyle</code>: The left side of this <code>Style</code>
     * instance.
     */
    private final ComponentSideStyle left;

    /**
     * Retrieves the left side of this <code>Style</code> instance.
     * 
     * @return <code>ComponentSideStyle</code>: The <code>left</code> field of this
     *         <code>Style</code> instance.
     */
    public ComponentSideStyle getLeft() {

        return left;
    }

    /**
     * <code>ComponentSideStyle</code>: The right side of this <code>Style</code>
     * instance.
     */
    private final ComponentSideStyle right;

    /**
     * Retrieves the right side of this <code>Style</code> instance.
     * 
     * @return <code>ComponentSideStyle</code>: The <code>right</code> field of this
     *         <code>Style</code> instance.
     */
    public ComponentSideStyle getRight() {

        return right;
    }

    private final Boolean focusable;

    public Boolean getFocusable() {

        return focusable;
    }

    /**
     * Creates a new instance of the <code>Style</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON to process.
     * @throws LoggedException Thrown if an error was raised while processing the
     *                         JSON.
     */
    public Style(TracedDictionary json) throws LoggedException {

        background = BackgroundStyle.createBackgroundStyle(json, "background");

        // Process the borders.
        BorderStyle border = BorderStyle.createBorderStyle(json, "border");

        BorderStyle borderVertical = BorderStyle.createBorderStyle(json, "borderVertical", border);

        BorderStyle borderTop = BorderStyle.createBorderStyle(json, "borderTop", borderVertical);
        BorderStyle borderBottom = BorderStyle.createBorderStyle(json, "borderBottom", borderVertical);

        BorderStyle borderHorizontal = BorderStyle.createBorderStyle(json, "borderHorizontal", border);

        BorderStyle borderLeft = BorderStyle.createBorderStyle(json, "borderLeft", borderHorizontal);
        BorderStyle borderRight = BorderStyle.createBorderStyle(json, "borderRight", borderHorizontal);

        // Process the border radii.
        ComponentSize borderRadius = ComponentSize.createSize(json, "borderRadius");

        ComponentSize borderRadiusTop = ComponentSize.createSize(json, "borderRadiusTop");
        ComponentSize borderRadiusBottom = ComponentSize.createSize(json, "borderRadiusBottom");
        ComponentSize borderRadiusLeft = ComponentSize.createSize(json, "borderRadiusLeft");
        ComponentSize borderRadiusRight = ComponentSize.createSize(json, "borderRadiusRight");

        ComponentSize borderRadiusTopLeft = findFirst(ComponentSize.createSize(json, "borderRadiusTopLeft"),
                borderRadiusTop, borderRadiusLeft, borderRadius);
        ComponentSize borderRadiusTopRight = findFirst(ComponentSize.createSize(json, "borderRadiusTopRight"),
                borderRadiusTop, borderRadiusRight, borderRadius);

        ComponentSize borderRadiusBottomRight = findFirst(ComponentSize.createSize(json, "borderRadiusBottomRight"),
                borderRadiusBottom, borderRadiusRight, borderRadius);
        ComponentSize borderRadiusBottomLeft = findFirst(ComponentSize.createSize(json, "borderRadiusBottomLeft"),
                borderRadiusBottom, borderRadiusLeft, borderRadius);

        // Process the margins.
        ComponentSize margin = ComponentSize.createSize(json, "margin");

        ComponentSize marginVertical = ComponentSize.createSize(json, "marginVertical", margin);

        ComponentSize marginTop = ComponentSize.createSize(json, "marginTop", marginVertical);
        ComponentSize marginBottom = ComponentSize.createSize(json, "marginBottom", marginVertical);

        ComponentSize marginHorizontal = ComponentSize.createSize(json, "marginHorizontal", margin);

        ComponentSize marginLeft = ComponentSize.createSize(json, "marginLeft", marginHorizontal);
        ComponentSize marginRight = ComponentSize.createSize(json, "marginRight", marginHorizontal);

        // Process the paddings.
        ComponentSize padding = ComponentSize.createSize(json, "padding");

        ComponentSize paddingVertical = ComponentSize.createSize(json, "paddingVertical", padding);

        ComponentSize paddingTop = ComponentSize.createSize(json, "paddingTop", paddingVertical);
        ComponentSize paddingBottom = ComponentSize.createSize(json, "paddingBottom", paddingVertical);

        ComponentSize paddingHorizontal = ComponentSize.createSize(json, "paddingHorizontal", padding);

        ComponentSize paddingLeft = ComponentSize.createSize(json, "paddingLeft", paddingHorizontal);
        ComponentSize paddingRight = ComponentSize.createSize(json, "paddingRight", paddingHorizontal);

        // Generate the sides.
        top = new ComponentSideStyle(borderTop, borderRadiusTopLeft, marginTop, paddingTop);
        bottom = new ComponentSideStyle(borderBottom, borderRadiusBottomRight, marginBottom, paddingBottom);
        left = new ComponentSideStyle(borderLeft, borderRadiusBottomLeft, marginLeft, paddingLeft);
        right = new ComponentSideStyle(borderRight, borderRadiusTopRight, marginRight, paddingRight);

        TracedEntry<Boolean> focusableEntry = json.getAsBoolean("focusable", true, null);
        focusable = focusableEntry.getValue();
    }

    public Style(List<Style> styles) {

        background = parseVal(styles, Style::getBackground);
        top = parseVal(styles, Style::getTop);
        bottom = parseVal(styles, Style::getBottom);
        left = parseVal(styles, Style::getLeft);
        right = parseVal(styles, Style::getRight);
        focusable = parseVal(styles, Style::getFocusable);
    }

    private <K> K parseVal(List<Style> styles, Function<Style, K> getter) {

        for (Style style : styles) {

            K val = getter.apply(style);
            if (val != null)
                return val;
        }

        return null;
    }

    /**
     * <code>Style.BackgroundStyle</code>: A class representing the style of a
     * background.
     */
    public static final class BackgroundStyle {

        /**
         * <code>Color</code>: The color of this <code>Style.BackgroundStyle</code>
         * instance.
         */
        private final Color color;

        /**
         * <code>String</code>: The texture of this <code>Style.BackgroundStyle</code>
         * instance.
         */
        private final String texture;

        /**
         * Creates a new instance of the <code>Style.BackgroundStyle</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The JSON to process.
         * @throws LoggedException Thrown if an error was raised while processing the
         *                         JSON.
         */
        private BackgroundStyle(TracedDictionary json) throws LoggedException {

            TracedEntry<Color> colorEntry = json.getAsColor("color", true, null);
            color = colorEntry.getValue();

            TracedEntry<String> textureEntry = json.getAsString("texture", true, null);
            texture = textureEntry.getValue();
        }

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
     * <code>Style.BorderStyle</code>: A class representing the style of a border.
     */
    public static final class BorderStyle {

        /**
         * <code>ComponentSize</code>: The width of this <code>Style.BorderStyle</code>
         * instance.
         */
        private final ComponentSize width;

        /**
         * <code>Color</code>: The color of this <code>Style.BorderStyle</code>
         * instance.
         */
        private final Color color;

        /**
         * Creates a new instance of the <code>Style.BorderStyle</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The JSON to process.
         * @throws LoggedException Thrown if an error was raised while processing the
         *                         JSON.
         */
        private BorderStyle(TracedDictionary json) throws LoggedException {

            width = ComponentSize.createSize(json, "size");

            TracedEntry<Color> colorEntry = json.getAsColor("color", true, null);
            color = colorEntry.getValue();
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Style.BorderStyle</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>Style.BorderStyle</code>: The parsed border style.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static BorderStyle createBorderStyle(TracedCollection collection, Object key) throws LoggedException {

            return createBorderStyle(collection, key, null);
        }

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
        public static BorderStyle createBorderStyle(TracedCollection collection, Object key, BorderStyle ifNull)
                throws LoggedException {

            return collection.get(key, List.of(

                    collection.dictCase(entry -> { // If the entry is a dictionary, process it into a border style.

                        TracedDictionary json = entry.getValue();
                        return new BorderStyle(json);
                    }), collection.nullCase(_ -> ifNull) // If the entry is missing, return null.
            ));
        }
    }

    /**
     * <code>Style.ComponentSideStyle</code>: A record representing the properties
     * of a single side (top, bottom, left, right) of a component.
     * 
     * @param border       <code>BorderStyle</code>: The border of this
     *                     <code>Style.ComponentSideStyle</code> instance.
     * @param borderRadius <code>ComponentSize</code>: The border radius of this
     *                     <code>Style.ComponentSideStyle</code> instance.
     * @param margin       <code>ComponentSize</code>: The margin of this
     *                     <code>Style.ComponentSideStyle</code> instance.
     * @param padding      <code>ComponentSize</code>: The padding of this
     *                     <code>Style.ComponentSideStyle</code> instance.
     */
    public static final record ComponentSideStyle(BorderStyle border, ComponentSize borderRadius, ComponentSize margin,
            ComponentSize padding) {
    }

    public static <K> K findFirst(K... values) {

        for (K value : values) {

            if (value != null) {

                return value;
            }
        }

        return null;
    }
}