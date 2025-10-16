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
public final record Style(Size x, Size y, Size width, Size height, Size minWidth, Size minHeight, SizeDimensions origin,
        Color backgroundColor, String backgroundTexture, TextureSize backgroundSize, BorderStyle borderTopStyle,
        Size borderTopWidth, Color borderTopColor, BorderStyle borderBottomStyle, Size borderBottomWidth,
        Color borderBottomColor, BorderStyle borderLeftStyle, Size borderLeftWidth, Color borderLeftColor,
        BorderStyle borderRightStyle, Size borderRightWidth, Color borderRightColor, SizeDimensions rTL,
        SizeDimensions rTR, SizeDimensions rBL, SizeDimensions rBR, Size marginTop, Size marginBottom, Size marginLeft,
        Size marginRight, Size paddingTop, Size paddingBottom, Size paddingLeft, Size paddingRight, Integer fontStyle,
        Integer fontWeight, Size fontSize, String fontFamily, Size lineHeight, TextAlign textAlign, Color color,
        TextureSize textureFit, WhiteSpace whiteSpace, OverflowWrap overflowWrap, TextOverflow textOverflow,
        Overflow overflowX, Overflow overflowY, Size gapWidth, Size gapHeight, Direction listDirection,
        Boolean propagateEvents, Display display, TriggerPhase triggerPhase) {

    /**
     * <code>Color</code>: A fully transparent color.
     */
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    /**
     * <code>Style</code>: A style without any defined attributes which will inherit
     * all default values.
     */
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null);

    /**
     * <code>Style</code>: A style which represents default string literal UI
     * components.
     */
    public static final Style STRING_STYLE = new Style(null, null, Size.FIT_CONTENT, Size.FIT_CONTENT, null, null, null,
            TRANSPARENT, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, Display.FLEX, null);

    /**
     * Creates a new instance of the <code>Style</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection to process.
     * @param key        <code>Object</code>: The key to retrieve.
     * @throws LoggedException Thrown if an error was raised while processing the
     *                         JSON.
     */
    public static final Style createStyle(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<TracedDictionary> entry = collection.getAsDict(key, true);
        if (!entry.containsValue()) {

            return EMPTY;
        }

        Size x = null;
        Size y = null;

        Size width = null;
        Size height = null;
        Size minWidth = null;
        Size minHeight = null;

        SizeDimensions origin = null;

        Color backgroundColor = null;
        String backgroundTexture = null;
        TextureSize backgroundSize = null;

        BorderStyle borderTopStyle = null;
        Size borderTopWidth = null;
        Color borderTopColor = null;

        BorderStyle borderBottomStyle = null;
        Size borderBottomWidth = null;
        Color borderBottomColor = null;

        BorderStyle borderLeftStyle = null;
        Size borderLeftWidth = null;
        Color borderLeftColor = null;

        BorderStyle borderRightStyle = null;
        Size borderRightWidth = null;
        Color borderRightColor = null;

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
        String fontFamily = null;

        Size lineHeight = null;
        TextAlign textAlign = null;
        Color color = null;

        TextureSize textureFit = null;

        WhiteSpace whiteSpace = null;
        OverflowWrap overflowWrap = null;
        TextOverflow textOverflow = null;
        Overflow overflowX = null;
        Overflow overflowY = null;

        Size gapWidth = null;
        Size gapHeight = null;
        Direction listDirection = null;

        Boolean propagateEvents = null;
        Display display = null;

        TriggerPhase triggerPhase = null;

        TracedDictionary json = entry.getValue();

        // Iterate through each property in the JSON.
        for (String property : json) {

            switch (property) {

            // Process the coordinates.
            case "x" -> x = Size.createSize(json, property);
            case "y" -> y = Size.createSize(json, property);

            // Process the size.
            case "width" -> width = Size.createSize(json, property);
            case "height" -> height = Size.createSize(json, property);
            case "minWidth" -> minWidth = Size.createSize(json, property);
            case "minHeight" -> minHeight = Size.createSize(json, property);

            case "origin" -> origin = SizeDimensions.createSizeDimensions(json, property);

            // Process the background.
            case "background" -> {

                TracedEntry<TracedDictionary> backgroundEntry = json.getAsDict(property, false);
                TracedDictionary backgroundJson = backgroundEntry.getValue();

                for (String backgroundProperty : backgroundJson) {

                    switch (backgroundProperty) {

                    case "color" -> backgroundColor = createColor(backgroundJson, backgroundProperty);
                    case "texture" -> backgroundTexture = createString(backgroundJson, backgroundProperty);
                    case "size" -> backgroundSize = TextureSize.createSize(backgroundJson, backgroundProperty);
                    }
                }
            }
            case "backgroundColor" -> backgroundColor = createColor(json, property);
            case "backgroundTexture" -> backgroundTexture = createString(json, property);
            case "backgroundSize" -> backgroundSize = TextureSize.createSize(json, property);

            // Process all four borders.
            case "border" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderLeftStyle = borderRightStyle = borderTopStyle = borderBottomStyle = BorderStyle
                            .createBorderStyle(borderJson, borderProperty);
                    case "width" -> borderLeftWidth = borderRightWidth = borderTopWidth = borderBottomWidth = Size
                            .createSize(borderJson, borderProperty);
                    case "color" -> borderLeftColor = borderRightColor = borderTopColor = borderBottomColor = createColor(
                            borderJson, borderProperty);
                    }
                }
            }
            case "borderStyle" -> borderLeftStyle = borderRightStyle = borderTopStyle = borderBottomStyle = BorderStyle
                    .createBorderStyle(json, property);
            case "borderWidth" -> borderLeftWidth = borderRightWidth = borderTopWidth = borderBottomWidth = Size
                    .createSize(json, property);
            case "borderColor" -> borderLeftColor = borderRightColor = borderTopColor = borderBottomColor = createColor(
                    json, property);

            // Process the top and bottom borders.
            case "borderVertical" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderTopStyle = borderBottomStyle = BorderStyle.createBorderStyle(borderJson,
                            borderProperty);
                    case "width" -> borderTopWidth = borderBottomWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderTopColor = borderBottomColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderVerticalStyle" -> borderTopStyle = borderBottomStyle = BorderStyle.createBorderStyle(json,
                    property);
            case "borderVerticalWidth" -> borderTopWidth = borderBottomWidth = Size.createSize(json, property);
            case "borderVerticalColor" -> borderTopColor = borderBottomColor = createColor(json, property);

            // Process the top border.
            case "borderTop" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderTopStyle = BorderStyle.createBorderStyle(borderJson, borderProperty);
                    case "width" -> borderTopWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderTopColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderTopStyle" -> borderTopStyle = BorderStyle.createBorderStyle(json, property);
            case "borderTopWidth" -> borderTopWidth = Size.createSize(json, property);
            case "borderTopColor" -> borderTopColor = createColor(json, property);

            // Process the bottom border.
            case "borderBottom" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderBottomStyle = BorderStyle.createBorderStyle(borderJson, borderProperty);
                    case "width" -> borderBottomWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderBottomColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderBottomStyle" -> borderBottomStyle = BorderStyle.createBorderStyle(json, property);
            case "borderBottomWidth" -> borderBottomWidth = Size.createSize(json, property);
            case "borderBottomColor" -> borderBottomColor = createColor(json, property);

            // Process the left and right border.
            case "borderHorizontal" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderLeftStyle = borderRightStyle = BorderStyle.createBorderStyle(borderJson,
                            borderProperty);
                    case "width" -> borderLeftWidth = borderRightWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderLeftColor = borderRightColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderHorizontalStyle" -> borderLeftStyle = borderRightStyle = BorderStyle.createBorderStyle(json,
                    property);
            case "borderHorizontalWidth" -> borderLeftWidth = borderRightWidth = Size.createSize(json, property);
            case "borderHorizontalColor" -> borderLeftColor = borderRightColor = createColor(json, property);

            // Process the left border.
            case "borderLeft" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderLeftStyle = BorderStyle.createBorderStyle(borderJson, borderProperty);
                    case "width" -> borderLeftWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderLeftColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderLeftStyle" -> borderLeftStyle = BorderStyle.createBorderStyle(json, property);
            case "borderLeftWidth" -> borderLeftWidth = Size.createSize(json, property);
            case "borderLeftColor" -> borderLeftColor = createColor(json, property);

            // Process the right border.
            case "borderRight" -> {

                TracedEntry<TracedDictionary> borderEntry = json.getAsDict(property, false);
                TracedDictionary borderJson = borderEntry.getValue();

                for (String borderProperty : borderJson) {

                    switch (borderProperty) {

                    case "style" -> borderRightStyle = BorderStyle.createBorderStyle(borderJson, borderProperty);
                    case "width" -> borderRightWidth = Size.createSize(borderJson, borderProperty);
                    case "color" -> borderRightColor = createColor(borderJson, borderProperty);
                    }
                }
            }
            case "borderRightStyle" -> borderRightStyle = BorderStyle.createBorderStyle(json, property);
            case "borderRightWidth" -> borderRightWidth = Size.createSize(json, property);
            case "borderRightColor" -> borderRightColor = createColor(json, property);

            // Process the border radius.
            case "borderRadius" -> rTL = rTR = rBL = rBR = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusTop" -> rTL = rTR = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusBottom" -> rBL = rBR = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusLeft" -> rTL = rBL = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusRight" -> rTR = rBR = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusTopLeft" -> rTL = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusTopRight" -> rTR = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusBottomLeft" -> rBL = SizeDimensions.createSizeDimensions(json, property);
            case "borderRadiusBottomRight" -> rBR = SizeDimensions.createSizeDimensions(json, property);

            // Process the margin.
            case "margin" -> marginTop = marginBottom = marginLeft = marginRight = Size.createSize(json, property);
            case "marginVertical" -> marginTop = marginBottom = Size.createSize(json, property);
            case "marginHorizontal" -> marginLeft = marginRight = Size.createSize(json, property);
            case "marginTop" -> marginTop = Size.createSize(json, property);
            case "marginBottom" -> marginBottom = Size.createSize(json, property);
            case "marginLeft" -> marginLeft = Size.createSize(json, property);
            case "marginRight" -> marginRight = Size.createSize(json, property);

            // Process the padding.
            case "padding" -> paddingTop = paddingBottom = paddingLeft = paddingRight = Size.createSize(json, property);
            case "paddingVertical" -> paddingTop = paddingBottom = Size.createSize(json, property);
            case "paddingHorizontal" -> paddingLeft = paddingRight = Size.createSize(json, property);
            case "paddingTop" -> paddingTop = Size.createSize(json, property);
            case "paddingBottom" -> paddingBottom = Size.createSize(json, property);
            case "paddingLeft" -> paddingLeft = Size.createSize(json, property);
            case "paddingRight" -> paddingRight = Size.createSize(json, property);

            // Process the font properties.
            case "font" -> {

                TracedEntry<TracedDictionary> fontEntry = json.getAsDict(property, false);
                TracedDictionary fontJson = fontEntry.getValue();

                for (String fontProperty : fontJson) {

                    switch (fontProperty) {

                    case "style" -> fontStyle = createFontStyle(fontJson, fontProperty);
                    case "weight" -> fontWeight = createFontWeight(fontJson, fontProperty);
                    case "size" -> fontSize = Size.createSize(fontJson, fontProperty);
                    case "family" -> fontFamily = createFontFamily(fontJson, fontProperty);
                    }
                }
            }
            case "fontStyle" -> fontStyle = createFontStyle(json, property);
            case "fontWeight" -> fontWeight = createFontWeight(json, property);
            case "fontSize" -> fontSize = Size.createSize(json, property);
            case "fontFamily" -> fontFamily = createFontFamily(json, property);

            // Process text properties.
            case "lineHeight" -> lineHeight = createLineHeight(json, property);
            case "color" -> color = createColor(json, property);
            case "textAlign" -> textAlign = TextAlign.createTextAlign(json, property);

            case "textureFit" -> textureFit = TextureSize.createSize(json, property);

            // Process the text wrapping and overflow.
            case "whiteSpace" -> whiteSpace = WhiteSpace.createWhiteSpace(json, property);
            case "overflowWrap" -> overflowWrap = OverflowWrap.createOverflowWrap(json, property);
            case "textOverflow" -> textOverflow = TextOverflow.createTextOverflow(json, property);

            // Process the overflow.
            case "overflow" -> overflowX = overflowY = Overflow.createOverflow(json, property);
            case "overflowX" -> overflowX = Overflow.createOverflow(json, property);
            case "overflowY" -> overflowY = Overflow.createOverflow(json, property);

            // Process the list element gap.
            case "gap" -> gapWidth = gapHeight = Size.createSize(json, property);
            case "gapWidth" -> gapWidth = Size.createSize(json, property);
            case "gapHeight" -> gapHeight = Size.createSize(json, property);

            // Process the list direction.
            case "listDirection" -> listDirection = Direction.createDirection(json, property);

            // Process the event propagation behavior.
            case "propagateEvents" -> propagateEvents = createBoolean(json, property);

            case "display" -> display = Display.createDisplay(json, property);

            case "triggerPhase" -> triggerPhase = TriggerPhase.createTriggerPhase(json, property);
            }
        }

        Style s = new Style(x, y, width, height, minWidth, minHeight, origin, backgroundColor, backgroundTexture,
                backgroundSize, borderTopStyle, borderTopWidth, borderTopColor, borderBottomStyle, borderBottomWidth,
                borderBottomColor, borderLeftStyle, borderLeftWidth, borderLeftColor, borderRightStyle,
                borderRightWidth, borderRightColor, rTL, rTR, rBL, rBR, marginTop, marginBottom, marginLeft,
                marginRight, paddingTop, paddingBottom, paddingLeft, paddingRight, fontStyle, fontWeight, fontSize,
                fontFamily, lineHeight, textAlign, color, textureFit, whiteSpace, overflowWrap, textOverflow, overflowX,
                overflowY, gapWidth, gapHeight, listDirection, propagateEvents, display, triggerPhase);

        return s;
    }

    /**
     * Creates a new instance of the <code>Style</code> class from a style list
     * ordered from lowest priority to highest, with a parent styles used for
     * implicit inherited values.
     * 
     * @param styles <code>List&lt;Style&gt;</code>: The style list to select from.
     * @param parent <code>Style</code>: The parent style to use when attempting to
     *               inherit values (e.g. font, text size & color).
     * @return <code>Style</code>: The parsed style.
     */
    public static final Style createStyle(List<Style> styles, Style parent) {

        // The latter elements in styles should be caught first.
        styles = styles.reversed();

        return new Style(

                // All sizing properties should be independent.
                parseVal(styles, Style::x, Size.NONE), parseVal(styles, Style::y, Size.NONE),
                parseVal(styles, Style::width, Size.FULL), parseVal(styles, Style::height, Size.AUTO),
                parseVal(styles, Style::minWidth, Size.NONE), parseVal(styles, Style::minHeight, Size.NONE),
                parseVal(styles, Style::origin, SizeDimensions.NONE),
                parseVal(styles, Style::backgroundColor, TRANSPARENT), parseVal(styles, Style::backgroundTexture, null),
                parseVal(styles, Style::backgroundSize, TextureSize.COVER),
                parseVal(styles, Style::borderTopStyle, BorderStyle.NONE),
                parseVal(styles, Style::borderTopWidth, Size.PX_1),
                parseVal(styles, Style::borderTopColor, Color.BLACK),
                parseVal(styles, Style::borderBottomStyle, BorderStyle.NONE),
                parseVal(styles, Style::borderBottomWidth, Size.PX_1),
                parseVal(styles, Style::borderBottomColor, Color.BLACK),
                parseVal(styles, Style::borderLeftStyle, BorderStyle.NONE),
                parseVal(styles, Style::borderLeftWidth, Size.PX_1),
                parseVal(styles, Style::borderLeftColor, Color.BLACK),
                parseVal(styles, Style::borderRightStyle, BorderStyle.NONE),
                parseVal(styles, Style::borderRightWidth, Size.PX_1),
                parseVal(styles, Style::borderRightColor, Color.BLACK),
                parseVal(styles, Style::rTL, SizeDimensions.NONE), parseVal(styles, Style::rTR, SizeDimensions.NONE),
                parseVal(styles, Style::rBL, SizeDimensions.NONE), parseVal(styles, Style::rBR, SizeDimensions.NONE),
                parseVal(styles, Style::marginTop, Size.NONE), parseVal(styles, Style::marginBottom, Size.NONE),
                parseVal(styles, Style::marginLeft, Size.NONE), parseVal(styles, Style::marginRight, Size.NONE),
                parseVal(styles, Style::paddingTop, Size.NONE), parseVal(styles, Style::paddingBottom, Size.NONE),
                parseVal(styles, Style::paddingLeft, Size.NONE), parseVal(styles, Style::paddingRight, Size.NONE),

                // All font properties should inherit parent properties.
                inheritVal(styles, Style::fontStyle, parent, Font.PLAIN),
                inheritVal(styles, Style::fontWeight, parent, Font.PLAIN),
                inheritVal(styles, Style::fontSize, parent, Size.FULL),
                inheritVal(styles, Style::fontFamily, parent, null),
                inheritVal(styles, Style::lineHeight, parent, new Size(value -> value * 1.2)),
                inheritVal(styles, Style::textAlign, parent, TextAlign.LEFT),
                inheritVal(styles, Style::color, parent, Color.BLACK),

                parseVal(styles, Style::textureFit, TextureSize.AUTO),

                inheritVal(styles, Style::whiteSpace, parent, WhiteSpace.NORMAL),
                inheritVal(styles, Style::overflowWrap, parent, OverflowWrap.NORMAL),
                inheritVal(styles, Style::textOverflow, parent, TextOverflow.CLIP),
                parseVal(styles, Style::overflowX, Overflow.CLIP), parseVal(styles, Style::overflowY, Overflow.CLIP),

                // All listing properties should be independent.
                parseVal(styles, Style::gapWidth, Size.NONE), parseVal(styles, Style::gapHeight, Size.NONE),
                parseVal(styles, Style::listDirection, Direction.VERTICAL),

                parseVal(styles, Style::propagateEvents, null),
                inheritVal(styles, Style::display, parent, Display.FLEX),

                parseVal(styles, Style::triggerPhase, TriggerPhase.RELEASE));
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
     * Finds the first defined property from a list of styles, or inherits from a
     * parent if absent.
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
    public static <K> K inheritVal(List<Style> styles, Function<Style, K> getter, Style parent, K ifNull) {

        // Use the parent values as the ifnull if it exists.
        K sub = getter.apply(parent);
        if (sub == null) {

            sub = ifNull;
        }
        return parseVal(styles, getter, sub);
    }

    /**
     * Parses a boolean from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>boolean</code>: The retrieved boolean.
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    public static final boolean createBoolean(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<Boolean> booleanEntry = collection.getAsBoolean(key, false, null);
        return booleanEntry.getValue();
    }

    /**
     * Parses a string from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>String</code>: The retrieved string.
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    public static final String createString(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<String> stringEntry = collection.getAsString(key, false, null);
        return stringEntry.getValue();
    }

    /**
     * Parses a color from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>Color</code>: The retrieved color.
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    public static final Color createColor(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<Color> colorEntry = collection.getAsColor(key, false, null);
        return colorEntry.getValue();
    }

    /**
     * <code>Size</code>: A class representing a dimension within a component.
     */
    public static final class Size {

        /**
         * <code>String</code>: The regular expression used to match a floating point
         * value.
         */
        private static final String NUMBER_PATTERN = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

        /**
         * <code>Size</code>: A constant representing a size of 0 pixels.
         */
        public static final Size NONE = new Size(_ -> 0.0);

        /**
         * <code>Size</code>: A constant representing a size of 100%.
         */
        public static final Size FULL = new Size(parent -> parent);

        /**
         * <code>Size</code>: A constant representing a size of 1 pixel.
         */
        public static final Size PX_1 = new Size(_ -> 1.0);

        /**
         * <code>Size</code>: A size representing an automatically-sized dimension,
         * which can expand to fit the content.
         */
        public static final Size AUTO = new Size(parent -> parent);

        /**
         * <code>Size</code>: A size representing an automatically-sized dimension,
         * which will contract to fit the content.
         */
        public static final Size FIT_CONTENT = new Size(parent -> parent);

        /**
         * <code>Function&lt;Integer, Double&gt;</code>: The operator used to calculate
         * the size of this <code>Size</code> instance.
         */
        private final Function<Double, Double> operator;

        /**
         * Retrieves the size of this <code>Size</code> instance in pixels.
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
         * Creates a new instance of the <code>Size</code> class.
         * 
         * @param operator <code>Function&lt;Double, Double&gt;</code>: The operator
         *                 used to calculate the size of this <code>Size</code>
         *                 instance.
         */
        private Size(Function<Double, Double> operator) {

            this.operator = operator;
        }

        /**
         * Parses a string-formatted size into a new <code>Size</code> instance.
         * 
         * @param entry <code>TracedEntry&lt;String&gt;</code>: The entry to parse.
         * @return <code>Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the entry.
         */
        public static final Size createStringSize(TracedEntry<String> entry) throws LoggedException {

            String full = entry.getValue();

            // If the full string is 'auto', return AUTO.
            if (full.equals("auto")) {

                return AUTO;
            }

            // If the full string is 'fitContent', return FIT_CONTENT.
            if (full.equals("fitContent")) {

                return FIT_CONTENT;
            }

            full = full.replaceAll(" ", "");
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

                String part = full.substring(start + 1, end);
                Function<Double, Double> operator = createOperator(entry, part);
                if (full.charAt(start) == '+') {

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

            // Strip the suffix off of the number section.
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
         * Parses a double into a new <code>Size</code> instance.
         * 
         * @param entry <code>TracedEntry&lt;Double&gt;</code>: The entry to parse.
         * @return <code>Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the entry.
         */
        public static final Size createDoubleSize(TracedEntry<Double> entry) throws LoggedException {

            double number = entry.getValue();
            if (number < 0) {

                throw new NumberBoundsException(number, entry);
            }

            return new Size(_ -> number);
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Size</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @return <code>Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static final Size createSize(TracedCollection collection, Object key) throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(Size::createStringSize), collection.doubleCase(Size::createDoubleSize)));
        }

        /**
         * Parses a value from a <code>TracedCollection</code> instance into a new
         * <code>Size</code> instance.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to search for.
         * @param ifNull     <code>Size</code>: The value to return if a size could not
         *                   be created.
         * @return <code>Size</code>: The parsed size.
         * @throws LoggedException Thrown if an error occurs while parsing the
         *                         collection.
         */
        public static final Size createSize(TracedCollection collection, Object key, Size ifNull)
                throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(Size::createStringSize), collection.doubleCase(Size::createDoubleSize),
                    collection.nullCase(_ -> ifNull)));
        }
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
         * entire graphic in the parent's bounds without distorting the texture.
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
         * parent's bounds without distorting the texture.
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
         * <code>TextureSize</code>: A texture size representing scaling to fill the
         * parent's bounds by distorting the texture.
         */
        public static final TextureSize FILL = new TextureSize() {

            @Override
            public int getWidth(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return parentWidth;
            }

            @Override
            public int getHeight(int textureWidth, int textureHeight, int parentWidth, int parentHeight) {

                return parentHeight;
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

                        // Use the original image size.
                        case "auto" -> AUTO;

                        // Maximize the image size while fitting it in the component.
                        case "contain" -> CONTAIN;

                        // Maximize the image size to fully cover the component.
                        case "cover" -> COVER;

                        // Distort the image to fully cover the component.
                        case "fill" -> FILL;

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
     * <code>BorderStyle</code>: An enum class representing the drawing method to
     * use on a border.
     */
    public static enum BorderStyle {

        /**
         * <code>BorderStyle</code>: An enum constant representing no border.
         */
        NONE,

        /**
         * <code>BorderStyle</code>: An enum constant representing a solid border.
         */
        SOLID;

        /**
         * Parses a collection into a border style.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse
         *                   from.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>BorderStyle</code>: The generated border style.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final BorderStyle createBorderStyle(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> borderEntry = collection.getAsString(key, false, null);
            String border = borderEntry.getValue();

            return switch (border) {

            case "none" -> NONE;
            case "solid" -> SOLID;
            default -> throw new UnexpectedValueException(borderEntry);
            };
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

    /**
     * Parses a font style from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>int</code>: The retrieved font size, as the
     *         <code>Font.PLAIN</code> or <code>Font.ITALIC</code> constants.
     * @throws LoggedException Thrown if the font size could not be parsed.
     */
    public static final int createFontStyle(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<String> fontStyleEntry = collection.getAsString(key, false, null);
        String fontStyle = fontStyleEntry.getValue();
        return switch (fontStyle) {

        case "normal" -> Font.PLAIN;
        case "italic" -> Font.ITALIC;
        default -> throw new UnexpectedValueException(fontStyleEntry);
        };
    }

    /**
     * Parses a font weight from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>int</code>: The retrieved font weight, as the
     *         <code>Font.PLAIN</code> or <code>Font.BOLD</code> constants.
     * @throws LoggedException Thrown if the font weight could not be parsed.
     */
    public static final int createFontWeight(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<String> fontWeightEntry = collection.getAsString(key, false, null);
        String fontWeight = fontWeightEntry.getValue();
        return switch (fontWeight) {

        case "normal" -> Font.PLAIN;
        case "bold" -> Font.BOLD;
        default -> throw new UnexpectedValueException(fontWeightEntry);
        };
    }

    /**
     * Parses a line height from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>Size</code>: The retrieved line height as a size.
     * @throws LoggedException Thrown if the line height could not be parsed.
     */
    public static final Size createLineHeight(TracedCollection collection, Object key) throws LoggedException {

        return collection.get(key, List.of(

                // A line width of x should really be 100x% of the text size.
                collection.doubleCase(entry -> {

                    double factor = entry.getValue();
                    return new Size(parent -> parent * factor);
                }),

                // String sizes should work normally.
                collection.stringCase(entry -> Size.createStringSize(entry))));
    }

    /**
     * Parses a font family from a collection.
     * 
     * @param collection <code>TracedCollection</code>: The collection to parse
     *                   from.
     * @param key        <code>Object</code>: The key to retrieve.
     * @return <code>int</code>: The topmost valid font family retrieved.
     * @throws LoggedException Thrown if the font family could not be parsed.
     */
    public static final String createFontFamily(TracedCollection collection, Object key) throws LoggedException {

        return collection.get(key, List.of(collection.stringCase(fontFamilyEntry -> {

            String fontFamily = fontFamilyEntry.getValue();
            return getFirstAvailableFont(List.of(fontFamily));
        }), collection.arrayCase(fontFamiliesEntry -> {

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

    /**
     * Retrieves the first valid font family from a list of options.
     * 
     * @param fontFamilies <code>String</code>: The font families to parse through.
     * @return <code>String</code>: The first font available in the Java Graphics
     *         Environment, or <code>null</code> if a font could not be found.
     */
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
     * <code>WhiteSpace</code>: An enum class representing the behavior for choosing
     * whether or not to overflow text.
     */
    public static enum WhiteSpace {

        /**
         * <code>WhiteSpace</code>: An enum constant representing overflowing when
         * necessary.
         */
        NORMAL,

        /**
         * <code>WhiteSpace</code>: An enum constant representing forcing all text onto
         * a single line.
         */
        NOWRAP;

        /**
         * Parses a collection into a white space.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting white space.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final WhiteSpace createWhiteSpace(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> whiteSpaceEntry = collection.getAsString(key, false, null);
            String whiteSpace = whiteSpaceEntry.getValue();

            return switch (whiteSpace) {

            case "normal" -> NORMAL;
            case "nowrap" -> NOWRAP;
            default -> throw new UnexpectedValueException(whiteSpaceEntry);
            };
        }
    }

    /**
     * <code>OverflowWrap</code>: An enum class representing the behavior for how to
     * split tokens when overflowing text.
     */
    public static enum OverflowWrap {

        /**
         * <code>OverflowWrap</code>: An enum constant representing overflowing between
         * words or hyphens.
         */
        NORMAL,

        /**
         * <code>OverflowWrap</code>: An enum constant representing overflowing in the
         * middle of words.
         */
        BREAK_WORD;

        /**
         * Parses a collection into an overflow wrap.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting overflow wrap.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final OverflowWrap createOverflowWrap(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> overflowWrapEntry = collection.getAsString(key, false, null);
            String overflowWrap = overflowWrapEntry.getValue();

            return switch (overflowWrap) {

            case "normal" -> NORMAL;
            case "breakWord" -> BREAK_WORD;
            default -> throw new UnexpectedValueException(overflowWrapEntry);
            };
        }
    }

    /**
     * <code>TextOverflow</code>: An enum class representing the behavior for how to
     * end a line of text when overflowing.
     */
    public static final record TextOverflow(String overflow) {

        /**
         * <code>TextOverflow</code>: A constant representing clipping a line of text
         * beyond its containment in text overflow.
         */
        public static final TextOverflow CLIP = new TextOverflow("clip");

        /**
         * <code>TextOverflow</code>: A constant representing ending a line of text with
         * ellipsis.
         */
        public static final TextOverflow ELLIPSIS = new TextOverflow("…");

        /**
         * Parses a collection into a text overflow.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting text overflow.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final TextOverflow createTextOverflow(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> textOverflowEntry = collection.getAsString(key, false, null);
            String textOverflow = textOverflowEntry.getValue();

            return switch (textOverflow) {
            case "clip" -> CLIP;
            case "ellipsis" -> ELLIPSIS;
            default -> new TextOverflow(textOverflow);
            };
        }
    }

    /**
     * <code>Overflow</code>: An enum class representing the behavior for when to
     * allow scrolling.
     */
    public static enum Overflow {

        /**
         * <code>Overflow</code>: An enum constant representing always clipping content
         * at the boundaries and hiding the scroll bar.
         */
        CLIP,

        /**
         * <code>Overflow</code>: An enum constant representing only showing the scroll
         * bar when the content overflows the content box.
         */
        AUTO,

        /**
         * <code>Overflow</code>: An enum constant representing always showing the
         * scroll bar, regardless of whether or not the content overflows the content
         * box.
         */
        SCROLL;

        /**
         * Parses a collection into an overflow.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting overflow.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final Overflow createOverflow(TracedCollection collection, Object key) throws LoggedException {

            TracedEntry<String> overflowEntry = collection.getAsString(key, false, null);
            String overflow = overflowEntry.getValue();
            return switch (overflow) {
            case "clip" -> CLIP;
            case "auto" -> AUTO;
            case "scroll" -> SCROLL;
            default -> throw new UnexpectedValueException(overflowEntry);
            };
        }
    }

    /**
     * <code>TextAlign</code>: An enum class representing the behavior for aligning
     * text.
     */
    public static enum TextAlign {

        /**
         * <code>TextAlign</code>: An enum constant representing aligning text to the
         * left side of the content box of a component.
         */
        LEFT,

        /**
         * <code>TextAlign</code>: An enum constant representing aligning text to the
         * right side of the content box of a component.
         */
        RIGHT,

        /**
         * <code>TextAlign</code>: An enum constant representing aligning text to the
         * center of the content box of a component.
         */
        CENTER,

        /**
         * <code>TextAlign</code>: An enum constant representing aligning text to both
         * the left and right sides of the content box of a component.
         */
        JUSTIFY;

        public static final TextAlign createTextAlign(TracedCollection collection, Object key) throws LoggedException {

            TracedEntry<String> textAlignEntry = collection.getAsString(key, false, null);
            String textAlign = textAlignEntry.getValue();
            return switch (textAlign) {
            case "left" -> LEFT;
            case "right" -> RIGHT;
            case "center" -> CENTER;
            case "justify" -> JUSTIFY;
            default -> throw new UnexpectedValueException(textAlignEntry);
            };
        }

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
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static Direction createDirection(TracedCollection collection, Object key) throws LoggedException {

            TracedEntry<String> directionEntry = collection.getAsString(key, false, null);
            String direction = directionEntry.getValue();

            return switch (direction) {

            case "horizontal" -> HORIZONTAL;
            case "vertical" -> VERTICAL;
            default -> throw new UnexpectedValueException(directionEntry);
            };
        };
    }

    /**
     * <code>Display</code>: An enum class representing the display method of a
     * component.
     */
    public static enum Display {

        /**
         * <code>Display</code>: An enum constant representing a non-resizing component.
         */
        BLOCK,

        /**
         * <code>Display</code>: An enum constant representing a resizing component.
         */
        FLEX;

        /**
         * Parses a collection into a block or flexible display.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>Direction</code>: The resulting display.
         * @throws LoggedException Thrown if the collection could not be parsed.
         */
        public static final Display createDisplay(TracedCollection collection, Object key) throws LoggedException {

            TracedEntry<String> displayEntry = collection.getAsString(key, false, null);
            String display = displayEntry.getValue();

            return switch (display) {

            case "block" -> BLOCK;
            case "flex" -> FLEX;
            default -> throw new UnexpectedValueException(displayEntry);
            };
        };
    }

    /**
     * <code>TriggerPhase</code>: An enum class representing the input trigger phase
     * of a component.
     */
    public static enum TriggerPhase {

        /**
         * <code>TriggerPhase</code>: An enum constant representing triggering the
         * action upon the press.
         */
        PRESS,

        /**
         * <code>TriggerPhase</code>: An enum constant representing triggering the
         * action upon the release.
         */
        RELEASE;

        /**
         * Parses a collection into a press or release.
         * 
         * @param collection <code>TracedCollection</code>: The collection to parse.
         * @param key        <code>Object</code>: The key to retrieve.
         * @return <code>TriggerPhase</code>: The resulting trigger phase.
         * @throws Display Thrown if the collection could not be parsed.
         */
        public static final TriggerPhase createTriggerPhase(TracedCollection collection, Object key)
                throws LoggedException {

            TracedEntry<String> triggerPhaseEntry = collection.getAsString(key, false, null);
            String triggerPhase = triggerPhaseEntry.getValue();

            return switch (triggerPhase) {

            case "press" -> PRESS;
            case "release" -> RELEASE;
            default -> throw new UnexpectedValueException(triggerPhaseEntry);
            };
        };
    }
}
