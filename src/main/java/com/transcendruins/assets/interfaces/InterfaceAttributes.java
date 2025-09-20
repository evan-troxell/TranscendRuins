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
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>InterfaceAttributes</code>: A class which represents the attributes of
 * a <code>InterfaceSchema</code> instance.
 */
public final class InterfaceAttributes extends AssetAttributes {

    public static final String LABEL = "label";
    public static final String BUTTON = "button";
    public static final String INPUT = "input";
    public static final String INTERFACE = "interface";

    public static final String DROPDOWN = "dropdown";
    public static final String SELECT = "select";

    public static final String LIST = "list";
    public static final String CONTAINER = "container";
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
    public StyleSet getStyles() {

        return styles;
    }

    /**
     * <code>InterfaceAttributes.ComponentSchema</code>: The body of this
     * <code>InterfaceAttributes</code> instance.
     */
    private final ComponentSchema body;

    /**
     * Retrieves the body of this <code>InterfaceAttributes</code> instance.
     * 
     * @return <code>ComponentSchema</code>: The <code>body</code> field of this
     *         <code>InterfaceAttributes</code> instance.
     */
    public ComponentSchema getBody() {

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

            TracedEntry<TracedDictionary> bodyEntry = json.getAsDict("body", false);
            TracedDictionary bodyJson = bodyEntry.getValue();

            body = createComponent(bodyJson);
        } else {

            body = null;
        }
    }

    /**
     * Creates a new instance of the
     * <code>InterfaceAttributes.ComponentSchema</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON to create the new
     *             <code>InterfaceAttributes.ComponentSchema</code> instance from.
     * @return <code>InterfaceAttributes.ComponentSchema</code>: The created loot
     *         schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         component.
     */
    public ComponentSchema createComponent(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        // Primitive component types.
        case LABEL -> new LabelComponentSchema(json);

        case BUTTON -> new ButtonComponentSchema(json);

        case INPUT -> new InputComponentSchema(json);

        // Dropdown container types.
        case DROPDOWN -> new DropdownComponentSchema(json);

        case SELECT -> new SelectComponentSchema(json);

        // Container types.
        case LIST -> new ListComponentSchema(json);

        case CONTAINER -> new ContainerComponentSchema(json);

        // Custom types.
        case INVENTORY -> new InventoryComponentSchema(json);

        case CRAFTING -> new CraftingComponentSchema(json);

        case INTERFACE -> new InterfaceComponentSchema(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>InterfaceAttributes.ComponentSchema</code>: A class representing the
     * schema of a visual component.
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

        public ComponentSchema(TracedDictionary json, String type) throws LoggedException {

            this.type = type;

            TracedEntry<String> idEntry = json.getAsString("id", true, null);
            id = idEntry.getValue();

            style = Style.createStyle(json, "style");
        }

        public final class LabelComponentSchema extends ComponentSchema {

            private final String text;

            public String getText() {

                return text;
            }

            private final String texture;

            public String getTexture() {

                return texture;
            }

            private final TRScript value;

            public TRScript getValue() {

                return value;
            }

            public LabelComponentSchema(TracedDictionary json) throws LoggedException {

                super(json, LABEL);

                TracedEntry<String> textEntry = json.getAsString("text", true, null);
                text = textEntry.getValue();

                TracedEntry<String> textureEntry = json.getAsString("texture", true, null);
                texture = textureEntry.getValue();

                value = new TRScript(json, "value");
            }
        }

        public final class ButtonComponentSchema extends ComponentSchema {

            private final String text;

            public String getText() {

                return text;
            }

            private final String texture;

            public String getTexture() {

                return texture;
            }

            private final TRScript value;

            public TRScript getValue() {

                return value;
            }

            private final ImmutableList<ComponentActionSchema> action;

            public ImmutableList<ComponentActionSchema> getAction() {

                return action;
            }

            public ButtonComponentSchema(TracedDictionary json) throws LoggedException {

                super(json, BUTTON);

                TracedEntry<String> textEntry = json.getAsString("text", true, null);
                text = textEntry.getValue();

                TracedEntry<String> textureEntry = json.getAsString("texture", true, null);
                texture = textureEntry.getValue();

                value = new TRScript(json, "value");

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

    public ComponentActionSchema createAction(TracedDictionary json) throws LoggedException {

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
}
