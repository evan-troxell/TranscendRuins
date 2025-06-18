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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
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

    /**
     * <code>StyleSet</code>: The styles of this <code>InterfaceAttributes</code>
     * instance.
     */
    private final StyleSet styles;

    /**
     * Retrieves the styles of this <code>InterfaceAttributes</code> instance.
     * 
     * @return <code>StyleSet</code>: The <code>styles</code> field of this
     *         <code>InterfaceAttributes</code> instance.
     */
    public StyleSet getStyles() {

        return styles;
    }

    /**
     * <code>InterfaceAttributes.ComponentSchema</code>: The root component of this
     * <code>InterfaceAttributes</code> instance.
     */
    private final ComponentSchema component;

    /**
     * Retrieves the root component of this <code>InterfaceAttributes</code>
     * instance.
     * 
     * @return <code>ComponentSchema</code>: The <code>component</code> field of
     *         this <code>InterfaceAttributes</code> instance.
     */
    public ComponentSchema getComponent() {

        return component;
    }

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

        // The styles can be overwritten, but note that each style is applied
        // independently, and the styles as a whole are not lost.
        styles = new StyleSet(json, "styles");

        // The components should only be defined once.
        if (isBase) {

            component = createComponent(json);
        } else {

            component = null;
        }
    }

    public ComponentSchema createComponent(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        // Primitive component types.
        case "label" -> new LabelComponentSchema(json);

        case "button" -> new ButtonComponentSchema(json);

        case "input" -> new InputComponentSchema(json);

        // Dropdown container types (and option).
        case "dropdown" -> new DropdownComponentSchema(json);

        case "select" -> new SelectComponentSchema(json);

        case "option" -> new OptionComponentSchema(json);

        // Container types.
        case "list" -> new ListComponentSchema(json);

        case "container" -> new ContainerComponentSchema(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>InterfaceAttributes.ComponentSchema</code>: A class representing the
     * schema of a visual component.
     */
    public abstract class ComponentSchema {

        private final String id;

        public String getId() {

            return id;
        }

        private final ComponentSize width;

        public ComponentSize getWidth() {

            return width;
        }

        private final ComponentSize height;

        public ComponentSize getHeight() {

            return height;
        }

        private final ComponentSize x;

        public ComponentSize getX() {

            return x;
        }

        private final ComponentSize y;

        public ComponentSize getY() {

            return y;
        }

        private final ImmutableList<StylePromise> style;

        public ImmutableList<StylePromise> getStyle() {

            return style;
        }

        private final ImmutableList<StylePromise> onHoverStyle;

        public ImmutableList<StylePromise> getOnHoverStyle() {

            return onHoverStyle;
        }

        private final ImmutableList<StylePromise> onPressStyle;

        public ImmutableList<StylePromise> getOnPressStyle() {

            return onPressStyle;
        }

        public ComponentSchema(TracedDictionary json) throws LoggedException {

            TracedEntry<String> idEntry = json.getAsString("id", true, null);
            id = idEntry.getValue();

            width = ComponentSize.createSize(json, "width", ComponentSize.FULL);
            height = ComponentSize.createSize(json, "height", ComponentSize.FULL);
            x = ComponentSize.createSize(json, "x", ComponentSize.NONE);
            y = ComponentSize.createSize(json, "y", ComponentSize.NONE);

            style = createStyle(json, "style");
            onHoverStyle = createStyle(json, "onHoverStyle");
            onPressStyle = createStyle(json, "onPressStyle");
        }

        private ImmutableList<StylePromise> createStyle(TracedCollection collection, Object key)
                throws LoggedException {

            return collection.get(key, List.of(

                    collection.stringCase(entry -> { // If the entry is a string, retrieve the corresponding style.

                        String styleKey = entry.getValue();

                        return new ImmutableList<>(createStyle(styleKey));
                    }),

                    collection.dictCase(entry -> { // If the entry is a dictionary, process it as a style.

                        TracedDictionary json = entry.getValue();
                        return new ImmutableList<>(createStyle(new Style(json)));
                    }),

                    collection.arrayCase(arrayEntry -> { // If the entry is an array, process each subentry as its own
                                                         // style.

                        ArrayList<StylePromise> styleList = new ArrayList<>();

                        TracedArray array = arrayEntry.getValue();
                        for (int i : array) {

                            StylePromise styleRetriever = array.get(i, List.of(

                                    array.stringCase(entry -> { // If the entry is a string, retrieve the corresponding
                                                                // style.

                                        String styleKey = entry.getValue();
                                        return createStyle(styleKey);
                                    }),

                                    array.dictCase(entry -> { // If the entry is a dictionary, process it as a style.

                                        TracedDictionary json = entry.getValue();
                                        return createStyle(new Style(json));
                                    })));

                            styleList.add(styleRetriever);
                        }

                        return new ImmutableList<>(styleList);
                    })

            ));
        }

        /**
         * Generates a style promise which should be internally fulfilled.
         * 
         * @param style <code>Style</code>: The style which fulfills the promise.
         * @return <code>InterfaceAttributes.StylePromise</code>: The resulting promise.
         */
        private StylePromise createStyle(Style style) {

            return new StylePromise() {

                @Override
                public Style getStyle(StyleSet... styles) {

                    return style;
                }
            };
        }

        /**
         * Generates a style promise which should be externally fulfilled.
         * 
         * @param style <code>String</code>: The name of the style which will be
         *              retrieved.
         * @return <code>InterfaceAttributes.StylePromise</code>: The resulting promise.
         */
        private StylePromise createStyle(String style) {

            return new StylePromise() {

                @Override
                public Style getStyle(StyleSet... styles) {

                    return StyleSet.getStyle(style, styles);
                }
            };
        }

        /**
         * <code>InterfaceAttributes.StylePromise</code>: An abstract class representing
         * a style to be retrieved. A style can be defined within a component or
         * referenced to an externally defined style, so this class should be extended
         * to provide different methods for handling each type of promise.
         */
        public abstract class StylePromise {

            /**
             * Retrieves the style awaited by this
             * <code>InterfaceAttributes.StylePromise</code> instance.
             * 
             * @param styles <code>StyleSet...</code>: The stack of style sets which may
             *               need to be searched through to find the style.
             * @return <code>Style</code>: The resulting style
             */
            public abstract Style getStyle(StyleSet... styles);
        }
    }
}
