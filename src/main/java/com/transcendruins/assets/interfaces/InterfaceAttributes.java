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
    public static final String LIST = "list";
    public static final String CONTAINER = "container";
    public static final String INTERFACE = "interface";

    public static final String INVENTORY = "inventory";
    public static final String CRAFTING = "crafting";

    public static final String OPEN_MENU = "openMenu";
    public static final String CLOSE_MENU = "closeMenu";

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

    public final ComponentSchema createDictComponent(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case TEXT -> new TextComponentSchema(json);

        case TEXTURE -> new TextureComponentSchema(json);

        case BUTTON -> new ButtonComponentSchema(json);

        case INPUT -> new InputComponentSchema(json);

        case DROPDOWN -> new DropdownComponentSchema(json);

        case SELECT -> new SelectComponentSchema(json);

        case LIST -> new ListComponentSchema(json);

        case CONTAINER -> new ContainerComponentSchema(json);

        case INTERFACE -> new InterfaceComponentSchema(json);

        case INVENTORY -> new InventoryComponentSchema(json);

        case CRAFTING -> new CraftingComponentSchema(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public final ComponentSchema createNonInteractiveComponent(TracedCollection collection, Object key)
            throws LoggedException {

        // If the child is a string, it is guaranteed to be non-interactive.
        if (collection.getType(key) == TracedCollection.JSONType.STRING) {

            TracedEntry<String> stringEntry = collection.getAsString(key, false, null);
            String string = stringEntry.getValue();
            return new StringComponentSchema(string);
        }

        TracedEntry<TracedDictionary> jsonEntry = collection.getAsDict(key, false);
        TracedDictionary json = jsonEntry.getValue();

        ComponentSchema child = createDictComponent(json);

        // If the child is a dictionary, check that it is text or a texture; else, throw
        // an error.
        if (child.getType().equals(TEXT) || child.getType().equals(TEXTURE)) {

            return child;
        }

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        throw new UnexpectedValueException(typeEntry);
    }

    /**
     * <code>ComponentSchema</code>: A class representing the schema of a component.
     */
    public abstract class ComponentSchema {

        private final String type;

        public final String getType() {

            return type;
        }

        private final String id;

        public final String getId() {

            return id;
        }

        private final Style style;

        public final Style getStyle() {

            return style;
        }

        private final TRScript value;

        public final TRScript getValue() {

            return value;
        }

        public ComponentSchema(String string, String type) {

            this.type = type;
            id = null;
            style = Style.EMPTY;
            value = new TRScript(string);
        }

        public ComponentSchema(TracedDictionary json, String type) throws LoggedException {

            this.type = type;

            TracedEntry<String> idEntry = json.getAsString("id", true, null);
            id = idEntry.getValue();

            style = Style.createStyle(json, "style");

            value = new TRScript(json, "value");
        }
    }

    public final class StringComponentSchema extends ComponentSchema {

        private final String string;

        public final String getString() {

            return string;
        }

        public StringComponentSchema(String string) {

            super(string, STRING);

            this.string = string;
        }
    }

    public final class TextComponentSchema extends ComponentSchema {

        private final StringComponentSchema text;

        public final StringComponentSchema getText() {

            return text;
        }

        public TextComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, TEXT);

            TracedEntry<String> textEntry = json.getAsString("text", true, null);
            String textString = textEntry.getValue();
            text = new StringComponentSchema(textString);
        }
    }

    public final class TextureComponentSchema extends ComponentSchema {

        private final String texture;

        public final String getTexture() {

            return texture;
        }

        public TextureComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, TEXTURE);

            TracedEntry<String> textureEntry = json.getAsString("texture", true, null);
            texture = textureEntry.getValue();
        }
    }

    public final class ButtonComponentSchema extends ComponentSchema {

        private final ImmutableList<ComponentSchema> components;

        public final ImmutableList<ComponentSchema> getComponents() {

            return components;
        }

        private final ImmutableList<ComponentActionSchema> action;

        public final ImmutableList<ComponentActionSchema> getAction() {

            return action;
        }

        public ButtonComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, BUTTON);

            components = json.get("components", List.of(

                    // Create multiple children.
                    json.arrayCase(componentsEntry -> {

                        ArrayList<ComponentSchema> componentsList = new ArrayList<>();
                        TracedArray componentsJson = componentsEntry.getValue();

                        // Process all of the children components.
                        for (int i : componentsJson) {

                            ComponentSchema component = createNonInteractiveComponent(componentsJson, i);
                            componentsList.add(component);
                        }

                        return new ImmutableList<>(componentsList);
                    }),

                    // Create a single child.
                    json.defaultCase(_ -> {

                        return new ImmutableList<>(createNonInteractiveComponent(json, "components"));
                    })));

            action = json.get("action", List.of(

                    // Process a dictionary into a single action.
                    json.dictCase(entry -> {

                        TracedDictionary actionJson = entry.getValue();
                        return new ImmutableList<>(createAction(actionJson));
                    }),

                    // Process an array into a list of actions.
                    json.arrayCase(entry -> {

                        ArrayList<ComponentActionSchema> actionsList = new ArrayList<>();

                        TracedArray actionsJson = entry.getValue();
                        for (int i : actionsJson) {

                            TracedEntry<TracedDictionary> actionEntry = actionsJson.getAsDict(i, false);
                            TracedDictionary actionJson = actionEntry.getValue();

                            actionsList.add(createAction(actionJson));
                        }

                        return new ImmutableList<>(actionsList);
                    })));
        }
    }

    public final class InterfaceComponentSchema extends ComponentSchema {

        private final AssetPresets presets;

        public AssetPresets getPresets() {

            return presets;
        }

        public InterfaceComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, INTERFACE);

            TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("interface", false, AssetType.INTERFACE);
            presets = presetsEntry.getValue();
            addAssetDependency(presets);
        }
    }

    public final ComponentActionSchema createAction(TracedDictionary json) throws LoggedException {

            TracedEntry<String> typeEntry = json.getAsString("type", false, null);
            String type = typeEntry.getValue();

            return switch (type) {

            case OPEN_MENU -> new OpenMenuComponentActionSchema(json);

            case CLOSE_MENU -> new CloseMenuComponentActionSchema(json);

            case SHOW_COMPONENT

            case HIDE_COMPONENT

            case COMPONENT_ACTION

            case SET_PROPERTY

            case SET_GLOBAL_PROPERTY

            default -> throw new UnexpectedValueException(typeEntry);
            };
        }

    public abstract class ComponentActionSchema {

        private final ImmutableList<TRScript> conditions;

        public final ImmutableList<TRScript> getConditions() {

            return conditions;
        }

        public ComponentActionSchema(TracedDictionary json) throws LoggedException {

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
        }
    }
}
