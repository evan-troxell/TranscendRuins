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
import java.util.HashSet;
import java.util.List;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>InterfaceAttributes</code>: A class which represents the attributes of
 * a <code>InterfaceSchema</code> instance.
 */
public final class InterfaceAttributes extends AssetAttributes {

    // Primative component types.
    public static final String STRING = "string";
    public static final String TEXT = "text";
    public static final String TEXTURE = "texture";

    // Traditional input component types.
    public static final String BUTTON = "button"; // Button input
    public static final String INPUT = "input"; // Text input

    // Dropdown component types.
    public static final String DROPDOWN = "dropdown"; // Drops down to reveal a list of elements.
    public static final String SELECT = "select"; // Drops down to reveal a list of elements that can be selected.

    // Container component types.
    public static final String CONTAINER = "container";
    public static final String LIST = "list";
    public static final String INTERFACE = "interface";

    public static final String INVENTORY = "inventory";
    public static final String CRAFTING = "crafting";

    /**
     * <code>StyleSet</code>: The style set of this <code>InterfaceAttributes</code>
     * instance.
     */
    private final StyleSet styles;

    /**
     * Retrieves the style set of this <code>InterfaceAttributes</code> instance.
     * 
     * @return <code>StyleSet</code>: The <code>styles</code> field of this
     *         <code>InterfaceAttributes</code> instance.
     */
    public final StyleSet getStyles() {

        return styles;
    }

    /**
     * <code>ComponentSchema</code>: The body of this
     * <code>InterfaceAttributes</code> instance.
     */
    private final ComponentSchema body;

    /**
     * Retrieves the body of this <code>InterfaceAttributes</code> instance.
     * 
     * @return <code>ComponentSchema</code>: The <code>body</code> field of this
     *         <code>InterfaceAttributes</code> instance.
     */
    public final ComponentSchema getBody() {

        return body;
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
        styles = new StyleSet(json, "style");

        // The body should only be defined once.
        if (isBase) {

            body = createComponent(json, "body");
        } else {

            body = null;
        }
    }

    /**
     * Creates a new instance of the <code>ComponentSchema</code> class.
     * 
     * @param collection <code>TracedDictionary</code>: The collection to create the
     *                   new <code>ComponentSchema</code> instance from.
     * @param key        <code>Object</code>: The key to retrieve from the
     *                   collection.
     * @return <code>ComponentSchema</code>: The created loot schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         component.
     */
    public final ComponentSchema createComponent(TracedCollection collection, Object key) throws LoggedException {

        return collection.get(key, List.of(

                // Handle a string literal.
                collection.stringCase(entry -> {

                    String string = entry.getValue();
                    return new StringComponentSchema(string);
                }),

                // Handle a dictionary component.
                collection.dictCase(entry -> {

                    TracedDictionary json = entry.getValue();
                    return createDictComponent(json);
                })));
    }

    /**
     * Creates a UI component schema from a dictionary.
     * 
     * @param json <code>TracedDictionary</code>: The dictionary to parse.
     * @return <code>ComponentSchema</code>: The generated schema.
     * @throws LoggedException Thrown if the dictionary could not be parsed.
     */
    public final ComponentSchema createDictComponent(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        // TODO Add rest of UI component types
        return switch (type) {

        case TEXT -> new TextComponentSchema(json);

        case TEXTURE -> new TextureComponentSchema(json);

        case BUTTON -> new ButtonComponentSchema(json);

        // case INPUT -> new InputComponentSchema(json);

        // case DROPDOWN -> new DropdownComponentSchema(json);

        // case SELECT -> new SelectComponentSchema(json);

        case CONTAINER -> new ContainerComponentSchema(json);

        case LIST -> new ListComponentSchema(json);

        case INTERFACE -> new InterfaceComponentSchema(json);

        // case INVENTORY -> new InventoryComponentSchema(json);

        // case CRAFTING -> new CraftingComponentSchema(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>ComponentSchema</code>: A class representing the schema of a UI
     * component.
     */
    public abstract class ComponentSchema {

        /**
         * <code>String</code>: The component type of this <code>ComponentSchema</code>
         * instance.
         */
        private final String type;

        /**
         * Retrieves the component type of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>String</code>: The <code>type</code> field of this
         *         <code>ComponentSchema</code> instance.
         */
        public final String getType() {

            return type;
        }

        /**
         * <code>String</code>: The component id of this <code>ComponentSchema</code>
         * instance.
         */
        private final String id;

        /**
         * Retrieves the component id of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>String</code>: The <code>id</code> field of this
         *         <code>ComponentSchema</code> instance.
         */
        public final String getId() {

            return id;
        }

        /**
         * <code>StyleSet</code>: The classes of this <code>ComponentSchema</code>
         * instance.
         */
        private final ImmutableSet<String> classes;

        /**
         * Retrieves the classes of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>StyleSet</code>: The <code>classes</code> field of this
         *         <code>ComponentSchema</code> instance.
         */
        public final ImmutableSet<String> getClasses() {

            return classes;
        }

        /**
         * <code>String</code>: The top-level style of this <code>ComponentSchema</code>
         * instance.
         */
        private final Style style;

        /**
         * Retrieves the top-level style of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>Style</code>: The <code>style</code> field of this
         *         <code>ComponentSchema</code> instance.
         */
        public final Style getStyle() {

            return style;
        }

        /**
         * <code>TRScript</code>: The value of this <code>ComponentSchema</code>
         * instance.
         */
        private final TRScript value;

        /**
         * Retrieves the value of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>TRScript</code>: The <code>value</code> field of this
         *         <code>ComponentSchema</code> instance.
         */
        public final TRScript getValue() {

            return value;
        }

        /**
         * <code>ArrayList&lt;ComponentSchema&gt;</code>: The children components of
         * this <code>ComponentSchema</code> instance. These will automatically be
         * generated and added when the parent is instantiated.
         */
        private final ArrayList<ComponentSchema> children = new ArrayList<>();

        /**
         * Adds a child to this <code>ComponentSchema</code> instance.
         * 
         * @param child <code>ComponentSchema</code>: The child component to add.
         */
        protected final void addChild(ComponentSchema child) {

            children.add(child);
        }

        /**
         * Retrieves the children of this <code>ComponentSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;ComponentSchema&gt;</code>: A copy of the
         *         <code>children</code> field of this code>ComponentSchema</code>
         *         instance.
         */
        public final ImmutableList<ComponentSchema> getChildren() {

            return new ImmutableList<>(children);
        }

        /**
         * Creates a new instance of the <code>ComponentSchema</code> class from a
         * string literal.
         * 
         * @param string <code>String</code>: The string value to use.
         */
        public ComponentSchema(String string) {

            type = STRING;
            id = null;
            classes = new ImmutableSet<>();
            style = Style.STRING_STYLE;
            value = new TRScript(string);
        }

        /**
         * Creates a new instance of the <code>ComponentSchema</code> class from a
         * dictionary.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @param type <code>String</code>: The component type to use.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public ComponentSchema(TracedDictionary json, String type) throws LoggedException {

            this.type = type;

            TracedEntry<String> idEntry = json.getAsString("id", true, null);
            id = idEntry.getValue();

            HashSet<String> classesList = new HashSet<>();

            // Process the class list.
            TracedEntry<TracedArray> classesEntry = json.getAsArray("classes", true);
            if (classesEntry.containsValue()) {

                TracedArray classesJson = classesEntry.getValue();
                for (int i : classesJson) {

                    TracedEntry<String> classEntry = classesJson.getAsString(i, false, null);
                    classesList.add(classEntry.getValue());
                }
            }

            classes = new ImmutableSet<>(classesList);

            style = Style.createStyle(json, "style");

            value = new TRScript(json, "value");
        }
    }

    /**
     * <code>StringComponentSchema</code>: A class representing the schema of a
     * string literal UI component.
     */
    public final class StringComponentSchema extends ComponentSchema {

        /**
         * <code>String</code>: The string value of this
         * <code>StringComponentSchema</code> instance.
         */
        private final String key;

        /**
         * Retrieves the string value of this <code>StringComponentSchema</code>
         * instance.
         * 
         * @return <code>String</code>: The <code>string</code> field of this
         *         <code>StringComponentSchema</code> instance.
         */
        public final String getKey() {

            return key;
        }

        /**
         * Creates a new instance of the <code>StringComponentSchema</code> class.
         * 
         * @param string <code>String</code>: The string value to use.
         */
        public StringComponentSchema(String string) {

            super(string);

            this.key = string;
        }
    }

    /**
     * <code>TextComponentSchema</code>: A class representing the schema of a text
     * UI component.
     */
    public final class TextComponentSchema extends ComponentSchema {

        /**
         * Creates a new instance of the <code>TextComponentSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public TextComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, TEXT);

            TracedEntry<String> textEntry = json.getAsString("text", false, null);
            StringComponentSchema text = new StringComponentSchema(textEntry.getValue());
            addChild(text);
        }
    }

    /**
     * <code>TextureComponentSchema</code>: A class representing the schema of a
     * texture UI component.
     */
    public final class TextureComponentSchema extends ComponentSchema {

        /**
         * <code>String</code>: The texture of this <code>TextureComponentSchema</code>
         * instance.
         */
        private final String texture;

        /**
         * Retrieves the texture of this <code>TextureComponentSchema</code> instance.
         * 
         * @return <code>String</code>: The <code>texture</code> field of this
         *         <code>TextureComponentSchema</code> instance.
         */
        public final String getTexture() {

            return texture;
        }

        /**
         * Creates a new instance of the <code>TextureComponentSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public TextureComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, TEXTURE);

            TracedEntry<String> textureEntry = json.getAsString("texture", false, null);
            texture = textureEntry.getValue();
        }
    }

    /**
     * <code>ButtonComponentSchema</code>: A class representing the schema of a
     * button UI component.
     */
    public final class ButtonComponentSchema extends ComponentSchema {

        /**
         * <code>ImmutableList&lt;TRScript&gt;</code>: The conditions required to be met
         * to apply the action of this <code>ButtonComponentSchema</code> instance.
         */
        private final ImmutableList<TRScript> conditions;

        /**
         * Retrieves the conditions required to be met to apply the action of this
         * <code>ButtonComponentSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;TRScript&gt;</code>: The
         *         <code>conditions</code> field of this
         *         <code>ButtonComponentSchema</code> instance.
         */
        public final ImmutableList<TRScript> getConditions() {

            return conditions;
        }

        /**
         * <code>ImmutableList&lt;ComponentActionSchema&gt;</code>: The action to run
         * when pressed.
         */
        private final ImmutableList<ComponentAction> action;

        /**
         * Retrieves the action to run when pressed.
         * 
         * @return <code>ImmutableList&lt;ComponentActionSchema&gt;</code>: The
         *         <code>action</code> field of this <code>ButtonComponentSchema</code>
         *         instance.
         */
        public final ImmutableList<ComponentAction> getAction() {

            return action;
        }

        /**
         * Creates a new instance of the <code>ButtonComponentSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public ButtonComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, BUTTON);

            if (json.containsKey("component")) {

                ComponentSchema component = createComponent(json, "component");
                addChild(component);
            }

            conditions = json.get("conditions", List.of(json.arrayCase(entry -> {

                ArrayList<TRScript> conditionsList = new ArrayList<>();

                TracedArray conditionsJson = entry.getValue();
                for (int i : conditionsJson) {

                    TracedEntry<TRScript> conditionEntry = conditionsJson.getAsScript(i, false);
                    TRScript condition = conditionEntry.getValue();
                    conditionsList.add(condition);
                }

                return new ImmutableList<>(conditionsList);
            }), json.nullCase(_ -> new ImmutableList<>()), json.scriptCase(entry -> {

                TRScript condition = entry.getValue();
                return new ImmutableList<>(condition);
            })));

            action = json.get("action", List.of(

                    // Process a dictionary into a single action.
                    json.dictCase(entry -> {

                        TracedDictionary actionJson = entry.getValue();
                        return new ImmutableList<>(ComponentAction.createAction(actionJson));
                    }),

                    // Process an array into a list of actions.
                    json.arrayCase(entry -> {

                        ArrayList<ComponentAction> actionsList = new ArrayList<>();

                        TracedArray actionsJson = entry.getValue();
                        for (int i : actionsJson) {

                            TracedEntry<TracedDictionary> actionEntry = actionsJson.getAsDict(i, false);
                            TracedDictionary actionJson = actionEntry.getValue();

                            actionsList.add(ComponentAction.createAction(actionJson));
                        }

                        return new ImmutableList<>(actionsList);
                    }),

                    // Process no actions.
                    json.nullCase(_ -> new ImmutableList<>())));
        }
    }

    /**
     * <code>InterfaceComponentSchema</code>: A class representing the schema of a
     * UI component which displays the content of another UI interface.
     */
    public final class InterfaceComponentSchema extends ComponentSchema {

        /**
         * <code>AssetPresets</code>: The presets of the interface to substitute for the
         * content of this <code>InterfaceComponentSchema</code> instance.
         */
        private final AssetPresets presets;

        /**
         * Retrieves the presets of the interface to substitute for the content of this
         * <code>InterfaceComponentSchema</code> instance.
         * 
         * @return <code>AssetPresets</code>: The <code>presets</code> field of this
         *         <code>InterfaceComponentSchema</code> instance.
         */
        public final AssetPresets getPresets() {

            return presets;
        }

        /**
         * Creates a new instance of the <code>InterfaceComponentSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public InterfaceComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, INTERFACE);

            TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("interface", false, AssetType.INTERFACE);
            presets = presetsEntry.getValue();
            addAssetDependency(presets);
        }
    }

    public final class ContainerComponentSchema extends ComponentSchema {

        public ContainerComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, CONTAINER);

            if (json.containsKey("components")) {

                TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
                TracedArray componentsJson = componentsEntry.getValue();

                for (int i : componentsJson) {

                    addChild(createComponent(componentsJson, i));
                }
            }
        }
    }

    public final class ListComponentSchema extends ComponentSchema {

        public ListComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, LIST);

            if (json.containsKey("components")) {

                TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
                TracedArray componentsJson = componentsEntry.getValue();

                for (int i : componentsJson) {

                    addChild(createComponent(componentsJson, i));
                }
            }
        }
    }
}
