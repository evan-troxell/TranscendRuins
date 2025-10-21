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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.items.ItemInstance;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.Style.Size;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
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
    public static final String ROTATE = "rotate";
    public static final String INTERFACE = "interface";

    // Special component types.
    public static final String GLOBAL_MAP = "globalMap";
    public static final String LOCATION_DISPLAY = "locationDisplay";
    public static final String INVENTORY_DISPLAY = "inventoryDisplay";
    public static final String INVENTORY = "inventory";

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

            body = createComponent(json, "body", this::addAssetDependency);
        } else {

            body = null;
        }
    }

    /**
     * Creates a new instance of the <code>ComponentSchema</code> class.
     * 
     * @param collection      <code>TracedDictionary</code>: The collection to
     *                        create the new <code>ComponentSchema</code> instance
     *                        from.
     * @param key             <code>Object</code>: The key to retrieve from the
     *                        collection.
     * @param dependencyAdder <code>Consumer&lt;AssetPresets&gt;</code>: The method
     *                        used to add dependencies to this
     *                        <code>InterfaceAttributes</code> instance.
     * @return <code>ComponentSchema</code>: The created loot schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         component.
     */
    public static final ComponentSchema createComponent(TracedCollection collection, Object key,
            Consumer<AssetPresets> dependencyAdder) throws LoggedException {

        return collection.get(key, List.of(

                // Handle a string literal.
                collection.stringCase(entry -> {

                    String string = entry.getValue();
                    return new StringComponentSchema(string);
                }),

                // Handle a dictionary component.
                collection.dictCase(entry -> {

                    TracedDictionary json = entry.getValue();
                    return createDictComponent(json, dependencyAdder);
                })));
    }

    /**
     * Creates a UI component schema from a dictionary.
     * 
     * @param json            <code>TracedDictionary</code>: The dictionary to
     *                        parse.
     * @param dependencyAdder <code>Consumer&lt;AssetPresets&gt;</code>: The method
     *                        used to add dependencies to this
     *                        <code>InterfaceAttributes</code> instance.
     * @return <code>ComponentSchema</code>: The generated schema.
     * @throws LoggedException Thrown if the dictionary could not be parsed.
     */
    public static final ComponentSchema createDictComponent(TracedDictionary json,
            Consumer<AssetPresets> dependencyAdder) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        // TODO Add rest of UI component types
        return switch (type) {

        case TEXT -> new TextComponentSchema(json);

        case TEXTURE -> new TextureComponentSchema(json);

        case BUTTON -> new ButtonComponentSchema(json, dependencyAdder);

        // case INPUT -> new InputComponentSchema(json);

        // case DROPDOWN -> new DropdownComponentSchema(json);

        // case SELECT -> new SelectComponentSchema(json);

        case CONTAINER -> new ContainerComponentSchema(json, dependencyAdder);

        case LIST -> new ListComponentSchema(json, dependencyAdder);

        case ROTATE -> new RotateComponentSchema(json, dependencyAdder);

        case INTERFACE -> new InterfaceComponentSchema(json, dependencyAdder);

        // case CRAFTING -> new CraftingComponentSchema(json);

        case GLOBAL_MAP -> new GlobalMapComponentSchema(json, dependencyAdder);

        case LOCATION_DISPLAY -> new LocationDisplayComponentSchema(json, dependencyAdder);

        case INVENTORY_DISPLAY -> new InventoryDisplayComponentSchema(json, dependencyAdder);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>ComponentSchema</code>: A class representing the schema of a UI
     * component.
     */
    public static abstract class ComponentSchema {

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
    public static final class StringComponentSchema extends ComponentSchema {

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
    public static final class TextComponentSchema extends ComponentSchema {

        /**
         * Creates a new instance of the <code>TextComponentSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The dictionary to parse.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public TextComponentSchema(TracedDictionary json) throws LoggedException {

            super(json, TEXT);

            TracedEntry<String> textEntry = json.getAsString("text", true, null);
            if (textEntry.containsValue()) {

                StringComponentSchema text = new StringComponentSchema(textEntry.getValue());
                addChild(text);
            }
        }
    }

    /**
     * <code>TextureComponentSchema</code>: A class representing the schema of a
     * texture UI component.
     */
    public static final class TextureComponentSchema extends ComponentSchema {

        /**
         * <code>TextureType</code>: The texture of this
         * <code>TextureComponentSchema</code> instance.
         */
        private final TextureType texture;

        /**
         * Retrieves the texture of this <code>TextureComponentSchema</code> instance.
         * 
         * @return <code>TextureType</code>: The <code>texture</code> field of this
         *         <code>TextureComponentSchema</code> instance.
         */
        public final TextureType getTexture() {

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

            texture = json.get("texture", List.of(

                    // Process a string literal texture.
                    json.stringCase(entry -> new StringTextureType(entry.getValue())),

                    // Process a dictionary texture.
                    json.dictCase(entry -> TextureType.createTextureType(entry.getValue()))));
        }
    }

    public static abstract class TextureType {

        private final String backup;

        public TextureType(String backup) {

            this.backup = backup;
        }

        public final ImageIcon getTexture(InterfaceInstance component, long componentId) {

            ImageIcon instanceTexture = getInstanceTexture(component, componentId);
            return instanceTexture == null && backup != null ? component.getWorld().getTexture(backup, componentId)
                    : instanceTexture;
        }

        protected abstract ImageIcon getInstanceTexture(InterfaceInstance component, long componentId);

        public static final TextureType createTextureType(TracedDictionary json) throws LoggedException {

            TracedEntry<String> typeEntry = json.getAsString("type", false, null);
            String type = typeEntry.getValue();

            return switch (type) {

            case "texture" -> {

                TracedEntry<String> textureEntry = json.getAsString("texture", false, null);
                yield new StringTextureType(textureEntry.getValue());
            }

            case "inventorySlot" -> json.get("slot",
                    List.of(json.intCase(entry -> new GridSlotTextureType(entry.getValue())),
                            json.stringCase(entry -> new NamedSlotTextureType(entry.getValue()))));

            default -> throw new UnexpectedValueException(typeEntry);
            };
        }
    }

    public static final class StringTextureType extends TextureType {

        public StringTextureType(String texture) {

            super(texture);
        }

        @Override
        protected ImageIcon getInstanceTexture(InterfaceInstance component, long componentId) {

            return null;
        }
    }

    public static final class GridSlotTextureType extends TextureType {

        private final int slot;

        public GridSlotTextureType(int slot) {

            super(null);
            this.slot = slot;
        }

        @Override
        protected ImageIcon getInstanceTexture(InterfaceInstance component, long componentId) {

            return component.getWorld().playerFunction(component.getPlayerId(), player -> {

                ItemInstance item = player.getEntity().getInventory().getItem(slot);
                if (item == null) {

                    return null;
                }

                return item.getIcon();
            });
        }
    }

    public static final class NamedSlotTextureType extends TextureType {

        private final String slot;

        public NamedSlotTextureType(String slot) {

            super(null);
            this.slot = slot;
        }

        @Override
        protected ImageIcon getInstanceTexture(InterfaceInstance component, long componentId) {

            return component.getWorld().playerFunction(component.getPlayerId(), player -> {

                ItemInstance item = player.getEntity().getInventory().getItem(slot);
                if (item == null) {

                    return null;
                }

                return item.getIcon();
            });
        }
    }

    /**
     * <code>ButtonComponentSchema</code>: A class representing the schema of a
     * button UI component.
     */
    public static final class ButtonComponentSchema extends ComponentSchema {

        /**
         * <code>ComponentActionSchema</code>: The action to run when pressed.
         */
        private final ComponentAction action;

        /**
         * Retrieves the action to run when pressed.
         * 
         * @return <code>ComponentActionSchema</code>: The <code>action</code> field of
         *         this <code>ButtonComponentSchema</code> instance.
         */
        public final ComponentAction getAction() {

            return action;
        }

        /**
         * Creates a new instance of the <code>ButtonComponentSchema</code> class.
         * 
         * @param json            <code>TracedDictionary</code>: The dictionary to
         *                        parse.
         * @param dependencyAdder <code>Consumer&lt;AssetPresets&gt;</code>: The method
         *                        used to add dependencies to this
         *                        <code>InterfaceAttributes</code> instance.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public ButtonComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, BUTTON);

            if (json.containsKey("component")) {

                ComponentSchema component = createComponent(json, "component", dependencyAdder);
                addChild(component);
            }

            action = new ComponentAction.ExecuteActionComponentAction(json);
        }
    }

    public static final class RotateComponentSchema extends ComponentSchema {

        private final double angle;

        public final double getAngle() {

            return angle;
        }

        private final Size centerX;

        public final Size getCenterX() {

            return centerX;
        }

        private final Size centerY;

        public final Size getCenterY() {

            return centerY;
        }

        public RotateComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, ROTATE);

            TracedEntry<Double> angleEntry = json.getAsDouble("angle", false, null);
            angle = angleEntry.getValue();

            centerX = Size.createSize(json, "centerX");
            centerY = Size.createSize(json, "centerY");

            ComponentSchema component = createComponent(json, "component", dependencyAdder);
            addChild(component);
        }
    }

    /**
     * <code>InterfaceComponentSchema</code>: A class representing the schema of a
     * UI component which displays the content of another UI interface.
     */
    public static final class InterfaceComponentSchema extends ComponentSchema {

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
         * @param json            <code>TracedDictionary</code>: The dictionary to
         *                        parse.
         * @param dependencyAdder <code>Consumer&lt;AssetPresets&gt;</code>: The method
         *                        used to add dependencies to this
         *                        <code>InterfaceAttributes</code> instance.
         * @throws LoggedException Thrown if the dictionary could not be parsed.
         */
        public InterfaceComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, INTERFACE);

            TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("interface", false, AssetType.INTERFACE);
            presets = presetsEntry.getValue();
            dependencyAdder.accept(presets);
        }
    }

    public static final class ContainerComponentSchema extends ComponentSchema {

        public ContainerComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, CONTAINER);

            if (json.containsKey("components")) {

                TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
                TracedArray componentsJson = componentsEntry.getValue();

                for (int i : componentsJson) {

                    addChild(createComponent(componentsJson, i, dependencyAdder));
                }
            }
        }
    }

    public static final class ListComponentSchema extends ComponentSchema {

        public ListComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, LIST);

            if (json.containsKey("components")) {

                TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
                TracedArray componentsJson = componentsEntry.getValue();

                for (int i : componentsJson) {

                    addChild(createComponent(componentsJson, i, dependencyAdder));
                }
            }
        }
    }

    public static final class GlobalMapComponentSchema extends ComponentSchema {

        private final ButtonComponentSchema enterButton;

        public final ButtonComponentSchema getEnterButton() {

            return enterButton;
        }

        public GlobalMapComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, GLOBAL_MAP);

            TracedEntry<TracedDictionary> componentsEntry = json.getAsDict("components", false);
            TracedDictionary componentsJson = componentsEntry.getValue();

            TracedEntry<TracedDictionary> enterButtonEntry = componentsJson.getAsDict("enterButton", false);
            TracedDictionary enterButtonJson = enterButtonEntry.getValue();
            enterButton = new ButtonComponentSchema(enterButtonJson, dependencyAdder);
        }
    }

    public static final class LocationDisplayComponentSchema extends ComponentSchema {

        private final TextComponentSchema nameText;

        public final TextComponentSchema getNameText() {

            return nameText;
        }

        private final TextComponentSchema descriptionText;

        public final TextComponentSchema getDescriptionText() {

            return descriptionText;
        }

        private final ButtonComponentSchema travelButton;

        public final ButtonComponentSchema getTravelButton() {

            return travelButton;
        }

        public LocationDisplayComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, LOCATION_DISPLAY);

            TracedEntry<TracedDictionary> componentsEntry = json.getAsDict("components", false);
            TracedDictionary componentsJson = componentsEntry.getValue();

            // nameText, descriptionText, travelButton
            TracedEntry<TracedDictionary> nameTextEntry = componentsJson.getAsDict("nameText", false);
            TracedDictionary nameTextJson = nameTextEntry.getValue();
            nameText = new TextComponentSchema(nameTextJson);

            TracedEntry<TracedDictionary> descriptionTextEntry = componentsJson.getAsDict("descriptionText", false);
            TracedDictionary descriptionTextJson = descriptionTextEntry.getValue();
            descriptionText = new TextComponentSchema(descriptionTextJson);

            TracedEntry<TracedDictionary> travelButtonEntry = componentsJson.getAsDict("travelButton", false);
            TracedDictionary travelButtonJson = travelButtonEntry.getValue();
            travelButton = new ButtonComponentSchema(travelButtonJson, dependencyAdder);
        }
    }

    public static final class InventoryDisplayComponentSchema extends ComponentSchema {

        public InventoryDisplayComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, INVENTORY_DISPLAY);
        }
    }

    public static final class InventoryComponentSchema extends ComponentSchema {

        private final ImmutableList<ImmutableList<Integer>> grid;

        public final ImmutableList<ImmutableList<Integer>> getGrid() {

            return grid;
        }

        private final ImmutableMap<String, Point> named;

        public final ImmutableMap<String, Point> getNamed() {

            return named;
        }

        private final Size slotSize;

        public final Size getSlotSize() {

            return slotSize;
        }

        public InventoryComponentSchema(TracedDictionary json, Consumer<AssetPresets> dependencyAdder)
                throws LoggedException {

            super(json, INVENTORY);

            if (json.containsKey("header")) {

                addChild(createComponent(json, "header", dependencyAdder));
            }

            grid = json.get("grid", List.of(json.dictCase(slotsEntry -> {
                TracedDictionary slotsJson = slotsEntry.getValue();

                TracedEntry<Integer> xEntry = slotsJson.getAsInteger("x", false, null, x -> x >= 0);
                int x = xEntry.getValue();

                TracedEntry<Integer> yEntry = slotsJson.getAsInteger("y", false, null, y -> y >= 0);
                int y = yEntry.getValue();

                TracedEntry<Integer> colsEntry = slotsJson.getAsInteger("cols", false, null, cols -> cols > 0);
                int width = colsEntry.getValue();

                TracedEntry<Integer> rowsEntry = slotsJson.getAsInteger("rows", false, null, rows -> rows > 0);
                int height = rowsEntry.getValue();

                TracedEntry<Integer> startEntry = slotsJson.getAsInteger("start", true, 0, rows -> rows >= 0);
                int start = startEntry.getValue();

                ArrayList<ImmutableList<Integer>> gridList = new ArrayList<>();
                gridList.add(new ImmutableList<>(x, y, width, height, start));

                return new ImmutableList<>(gridList);
            }), json.arrayCase(entry -> {

                ArrayList<ImmutableList<Integer>> gridList = new ArrayList<>();

                TracedArray gridJson = entry.getValue();
                for (int i : gridJson) {

                    TracedEntry<TracedDictionary> slotsEntry = gridJson.getAsDict(i, false);
                    TracedDictionary slotsJson = slotsEntry.getValue();

                    TracedEntry<Integer> xEntry = slotsJson.getAsInteger("x", false, null, x -> x >= 0);
                    int x = xEntry.getValue();

                    TracedEntry<Integer> yEntry = slotsJson.getAsInteger("y", false, null, y -> y >= 0);
                    int y = yEntry.getValue();

                    TracedEntry<Integer> colsEntry = slotsJson.getAsInteger("cols", false, null, cols -> cols > 0);
                    int width = colsEntry.getValue();

                    TracedEntry<Integer> rowsEntry = slotsJson.getAsInteger("rows", false, null, rows -> rows > 0);
                    int height = rowsEntry.getValue();

                    TracedEntry<Integer> startEntry = slotsJson.getAsInteger("start", true, 0, rows -> rows >= 0);
                    int start = startEntry.getValue();

                    ImmutableList<Integer> slotsList = new ImmutableList<>(x, y, width, height, start);

                    gridList.add(slotsList);
                }

                return new ImmutableList<>(gridList);
            }), json.nullCase(_ -> new ImmutableList<>())

            ));

            LinkedHashMap<String, Point> namedMap = new LinkedHashMap<>();
            TracedEntry<TracedDictionary> namedEntry = json.getAsDict("named", true);
            if (namedEntry.containsValue()) {

                TracedDictionary namedJson = namedEntry.getValue();
                for (String name : namedJson) {

                    Point point = namedJson.get(name, List.of(namedJson.arrayCase(entry -> {

                        TracedArray pointJson = entry.getValue();
                        if (pointJson.size() != 2) {

                            throw new CollectionSizeException(entry, pointJson);
                        }

                        TracedEntry<Integer> xEntry = pointJson.getAsInteger(0, false, null, x -> x >= 0);
                        int x = xEntry.getValue();

                        TracedEntry<Integer> yEntry = pointJson.getAsInteger(1, false, null, y -> y >= 0);
                        int y = yEntry.getValue();

                        return new Point(x, y);
                    }), namedJson.dictCase(entry -> {

                        TracedDictionary pointJson = entry.getValue();

                        TracedEntry<Integer> xEntry = pointJson.getAsInteger("x", false, null, x -> x >= 0);
                        int x = xEntry.getValue();

                        TracedEntry<Integer> yEntry = pointJson.getAsInteger("y", false, null, y -> y >= 0);
                        int y = yEntry.getValue();

                        return new Point(x, y);
                    })));

                    namedMap.put(name, point);
                }
            }

            named = new ImmutableMap<>(namedMap);

            slotSize = Size.createSize(json, "slotSize");
        }
    }
}
