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

package com.transcendruins.assets.layouts;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.AssetType;
import static com.transcendruins.assets.AssetType.ELEMENT;
import static com.transcendruins.assets.AssetType.ENTITY;
import static com.transcendruins.assets.AssetType.LAYOUT;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.layouts.placement.GenerationPlacement;
import com.transcendruins.assets.layouts.placement.GenerationShapeInstance;
import com.transcendruins.assets.layouts.placement.PlacementArea;
import com.transcendruins.assets.modelassets.elements.ElementContext;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityContext;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import static com.transcendruins.utilities.json.TracedCollection.JSONType.STRING;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.ContinuousRange;
import com.transcendruins.utilities.selection.DiscreteRange;
import com.transcendruins.utilities.selection.EliminationRoll;
import com.transcendruins.utilities.selection.SelectionType;
import static com.transcendruins.utilities.selection.SelectionType.createSelectionType;
import com.transcendruins.utilities.selection.WeightedRoll;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>LayoutAttributes</code>: A class which represents the attributes of a
 * <code>LayoutSchema</code> instance.
 */
public final class LayoutAttributes extends AssetAttributes {

    private final ImmutableMap<AssetType, ImmutableMap<String, AssetPresets>> definitions;

    private AssetPresets getDefinition(TracedCollection collection, Object key, AssetType type) throws LoggedException {

        if (collection.getType(key) == TracedCollection.JSONType.STRING) {

            TracedEntry<String> assetEntry = collection.getAsString(key, false, null);
            String asset = assetEntry.getValue();

            if (definitions.get(type).containsKey(asset)) {

                return definitions.get(type).get(asset);
            }
        }

        TracedEntry<AssetPresets> presetsEntry = collection.getAsPresets(key, false, type);
        return presetsEntry.getValue();
    }

    private final WeightedRoll<GenerationPlacement> spawn;

    public final WeightedRoll<GenerationPlacement> getSpawn() {

        return spawn;
    }

    private final GenerationLayout generation;

    public final GenerationLayout getGeneration() {

        return generation;
    }

    /**
     * Compiles this <code>LayoutAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>LayoutAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>LayoutAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>LayoutAttributes</code> instance is the base attribute
     *               set of a <code>LayoutAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>LayoutAttributes</code> instance.
     */
    public LayoutAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        HashMap<AssetType, ImmutableMap<String, AssetPresets>> definitionsMap = new HashMap<>();
        Map<AssetType, String> categories = Map.of(ELEMENT, "elements", ENTITY, "entities", LAYOUT, "layouts");

        TracedEntry<TracedDictionary> definitionsEntry = json.getAsDict("definitions", true);
        if (definitionsEntry.containsValue()) {

            TracedDictionary definitionsJson = definitionsEntry.getValue();
            for (Map.Entry<AssetType, String> category : categories.entrySet()) {

                AssetType type = category.getKey();
                String categoryKey = category.getValue();

                HashMap<String, AssetPresets> categoryMap = new HashMap<>();

                TracedEntry<TracedDictionary> categoryEntry = definitionsJson.getAsDict(categoryKey, true);
                if (categoryEntry.containsValue()) {

                    TracedDictionary categoryJson = categoryEntry.getValue();
                    for (String key : categoryJson) {

                        TracedEntry<AssetPresets> assetEntry = categoryJson.getAsPresets(key, false, type);
                        AssetPresets asset = assetEntry.getValue();
                        addAssetDependency(asset);

                        categoryMap.put(key, asset);
                    }
                }

                definitionsMap.put(type, new ImmutableMap<>(categoryMap));
            }
        } else {

            categories.keySet().forEach(type -> definitionsMap.put(type, new ImmutableMap<>()));
        }

        definitions = new ImmutableMap<>(definitionsMap);

        spawn = GenerationPlacement.createPlacement(json, "spawn");
        generation = createGenerationLayout(json);
    }

    public final GenerationLayout createGenerationLayout(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();
        return switch (type) {

        case "element" -> new AssetGenerationLayout(json, ELEMENT);

        case "entity" -> new AssetGenerationLayout(json, ENTITY);

        case "layout" -> new LayoutGenerationLayout(json);

        case "distributed" -> new DistributionGenerationLayout(json);

        case "grid" -> new GridGenerationLayout(json);

        case "blueprint" -> new BlueprintGenerationLayout(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final WeightedRoll<Integer> getDirection(TracedCollection collection, Object key)
            throws LoggedException {

        return collection.getAsStringRoll(key, true, World.EAST, entry -> switch (entry.getValue()) {

        case "east" -> World.EAST;
        case "north" -> World.NORTH;
        case "west" -> World.WEST;
        case "south" -> World.SOUTH;
        default -> throw new UnexpectedValueException(entry);
        }, (stringJson, stringKey) -> switch (stringKey) {

        case "east" -> World.EAST;
        case "north" -> World.NORTH;
        case "west" -> World.WEST;
        case "south" -> World.SOUTH;
        default -> throw new KeyNameException(stringJson, stringKey);
        });
    }

    /**
     * <code>GenerationLayout</code>: A class representing any generation layout.
     */
    public static abstract class GenerationLayout {

        /**
         * <code>DiscreteRange</code>: The count of this <code>GenerationLayout</code>
         * instance. This value represents the range of how many times this
         * <code>GenerationLayout</code> instance can be selected in a limited
         * selection, but a fixed count in a full selection.
         */
        private final DiscreteRange count;

        private final WeightedRoll<GenerationPlacement> placement;

        private final WeightedRoll<Integer> rotation;

        public GenerationLayout(TracedDictionary json) throws LoggedException {

            count = DiscreteRange.createRange(json, "count", true, 1, num -> num > 0);

            placement = GenerationPlacement.createPlacement(json, "placement");

            rotation = getDirection(json, "rotation");
        }

        public final int[] generate(AreaGrid parent, DeterministicRandom random, GlobalLocationInstance location) {

            int num = count.get(random.next());
            int failures = 0;

            for (int i = 0; i < num; i++) {

                AreaGrid area = generateContent(parent, random, location, null);
                if (area == null) {

                    continue;
                }

                int direction = rotation.get(random.next());
                area.rotate(direction);

                GenerationPlacement placementOption = placement.get(random.next());
                GenerationShapeInstance shape = placementOption.generateShape(parent, area.getWidth(), area.getLength(),
                        random);

                Point point = shape.getPoint(p -> parent.canAddAt(area, p.x, p.y), random);
                if (point == null) {

                    failures++;
                    continue;
                }

                parent.addArea(area, point.x, point.y);
            }

            return new int[] { num, failures };
        }

        public abstract AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn);
    }

    public static final record AssetConnection(AssetType type, Identifier identifier, WeightedRoll<String> connection) {

    }

    public final AssetConnection createAssetConnection(TracedDictionary json, AssetType type, String typeKey)
            throws LoggedException {

        Identifier identifier = null;
        if (json.getType(typeKey) == STRING) {

            TracedEntry<String> definitionEntry = json.getAsString(typeKey, false, null);
            String definitionString = definitionEntry.getValue();
            if (definitions.get(type).containsKey(definitionString)) {

                identifier = definitions.get(type).get(definitionString).getIdentifier();
            }
        }

        if (identifier == null) {

            TracedEntry<Identifier> identifierEntry = json.getAsMetadata(typeKey, false, false);
            addAssetDependency(type, identifierEntry);
            identifier = identifierEntry.getValue();
        }

        WeightedRoll<String> connection = json.getAsStringRoll("connection", false, null, entry -> entry.getValue(),
                (dict, string) -> string);

        return new AssetConnection(type, identifier, connection);
    }

    public final class AssetGenerationLayout extends GenerationLayout {

        private final AssetPresets presets;

        private final Vector tileOffset;

        private final ContinuousRange heading;

        private final ImmutableSet<String> tag;

        private final WeightedRoll<AssetConnection> connection;

        public AssetGenerationLayout(TracedDictionary json, AssetType type) throws LoggedException {

            super(json);

            String typeKey = type.name().toLowerCase();
            presets = getDefinition(json, typeKey, type);

            TracedEntry<Vector> tileOffsetEntry = json.getAsVector("tileOffset", true, 3, null, null);
            tileOffset = tileOffsetEntry.containsValue() ? tileOffsetEntry.getValue() : Vector.IDENTITY_VECTOR;

            heading = ContinuousRange.createRange(json, "heading", true, 0, num -> 0 <= num && num < 360);

            tag = json.get("tag",
                    List.of(json.stringCase(entry -> new ImmutableSet<>(entry.getValue())), json.arrayCase(entry -> {

                        HashSet<String> tagSet = new HashSet<>();

                        TracedArray tagJson = entry.getValue();
                        for (int i : tagJson) {

                            TracedEntry<String> tagEntry = tagJson.getAsString(i, false, null);
                            tagSet.add(tagEntry.getValue());
                        }

                        return new ImmutableSet<>(tagSet);
                    }), json.nullCase(_ -> null)));

            connection = json.getAsRoll("connection", true, null, entry -> {

                TracedDictionary connectionJson = entry.getValue();

                TracedEntry<String> connectionTypeEntry = connectionJson.getAsString("type", false, null);

                String connectionTypeKey = connectionTypeEntry.getValue();

                AssetType connectionType = switch (connectionTypeKey) {

                case "element" -> ELEMENT;
                case "entity" -> ENTITY;
                default -> throw new UnexpectedValueException(connectionTypeEntry);
                };

                if (type == ELEMENT && connectionType == ENTITY) {

                    throw new UnexpectedValueException(connectionTypeEntry);
                }

                return createAssetConnection(connectionJson, connectionType, connectionTypeKey);
            });
        }

        @Override
        public final AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn) {

            PrimaryAssetInstance asset;

            AssetType type = presets.getType();
            if (type == ELEMENT) {

                ElementContext context = new ElementContext(presets, location, tileOffset);
                ElementInstance element = context.instantiate();

                asset = element;
            } else {

                EntityContext context = new EntityContext(presets, location);
                EntityInstance entity = context.instantiate();

                double degrees = heading.get(random.next());
                entity.rotate(degrees);

                entity.setPosition(0, 0);
                entity.translate(tileOffset);

                asset = entity;
            }

            if (asset == null) {

                return null;
            }

            if (connection == null) {

                return new AreaGrid(asset, tag, spawn);
            }

            boolean selected = false;
            EliminationRoll<AssetConnection> assetConnectionOptions = new EliminationRoll<>(connection, random);
            for (AssetConnection assetOption : assetConnectionOptions) {

                Identifier identifier = assetOption.identifier();
                AssetType assetOptionType = assetOption.type();

                EliminationRoll<String> connectionOptions = new EliminationRoll<>(assetOption.connection(), random);

                if (assetOptionType == ELEMENT) {

                    ArrayList<ElementInstance> elementOptions = new ArrayList<>(parent.getElements(identifier));
                    random.shuffle(elementOptions);

                    for (String connectionOption : connectionOptions) {

                        for (ElementInstance elementOption : elementOptions) {

                            if (!elementOption.containsBone(connectionOption)) {

                                continue;
                            }

                            if (!elementOption.containsPrimaryAssetChild(connectionOption)) {

                                continue;
                            }

                            switch (asset) {

                            case ElementInstance element -> {

                                parent.addElement(element, tag);
                                elementOption.addModelChild(element, connectionOption);
                            }

                            case EntityInstance entity -> {

                                parent.addEntity(entity, tag);
                                elementOption.addModelChild(entity, connectionOption);
                            }

                            default -> {
                            }
                            }

                            selected = true;
                            break;
                        }

                        if (selected) {

                            break;
                        }
                    }
                } else {

                    ArrayList<EntityInstance> entityOptions = new ArrayList<>(parent.getEntities(identifier));
                    random.shuffle(entityOptions);

                    for (String connectionOption : connectionOptions) {

                        for (EntityInstance entityOption : entityOptions) {

                            if (!entityOption.containsBone(connectionOption)) {

                                continue;
                            }

                            if (!entityOption.containsPrimaryAssetChild(connectionOption)) {

                                continue;
                            }

                            if (asset instanceof EntityInstance entity) {

                                parent.addEntity(entity, tag);
                                entityOption.addModelChild(entity, connectionOption);
                            }

                            selected = true;
                            break;
                        }

                        if (selected) {

                            break;
                        }
                    }
                }

                if (selected) {

                    break;
                }
            }

            return null;
        }
    }

    public final class LayoutGenerationLayout extends GenerationLayout {

        private final AssetPresets presets;

        public LayoutGenerationLayout(TracedDictionary json) throws LoggedException {

            super(json);

            presets = json.get("layout", List.of(

                    // Attempt to retrieve the layout from the definitions.
                    json.stringCase(entry -> {

                        String layoutString = entry.getValue();
                        if (definitions.get(LAYOUT).containsKey(layoutString)) {

                            return definitions.get(LAYOUT).get(layoutString);
                        }

                        // Construct the presets.
                        TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("layout", false, LAYOUT);
                        return presetsEntry.getValue();
                    }),

                    // Construct the presets.
                    json.presetsCase(TracedEntry::getValue, LAYOUT)));
        }

        @Override
        public final AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn) {

            LayoutContext context = new LayoutContext(presets, location);
            LayoutInstance layout = context.instantiate();

            AreaGrid area = layout.generate();
            AreaGrid wrapper = new AreaGrid(new Dimension(area.getWidth(), area.getLength()), spawn);

            wrapper.addArea(area, 0, 0);

            return wrapper;
        }
    }

    public final class DistributionGenerationLayout extends GenerationLayout {

        private final DiscreteRange width;

        private final DiscreteRange length;

        private final DiscreteRange rolls;

        private final SelectionType iterationType;

        private final ImmutableList<DistributionComponent> components;

        public DistributionGenerationLayout(TracedDictionary json) throws LoggedException {

            super(json);

            width = DiscreteRange.createRange(json, "width", false, -1, num -> num > 0);
            length = DiscreteRange.createRange(json, "length", false, -1, num -> num > 0);

            rolls = DiscreteRange.createRange(json, "rolls", true, 1, num -> num > 0);
            iterationType = createSelectionType(json, "iterationType");

            ArrayList<DistributionComponent> componentsList = new ArrayList<>();

            TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
            TracedArray componentsJson = componentsEntry.getValue();
            for (int i : componentsJson) {

                TracedEntry<TracedDictionary> componentEntry = componentsJson.getAsDict(i, false);
                TracedDictionary componentJson = componentEntry.getValue();

                componentsList.add(createDistributionComponent(componentJson));
            }
            components = new ImmutableList<>(componentsList);
        }

        private final record DistributionComponent(GenerationLayout component, double chance, DiscreteRange limit) {
        }

        private DistributionComponent createDistributionComponent(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedDictionary> componentEntry = json.getAsDict("component", false);
            TracedDictionary componentJson = componentEntry.getValue();

            GenerationLayout component = createGenerationLayout(componentJson);

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
            double chance = chanceEntry.getValue();

            DiscreteRange limit = DiscreteRange.createRange(componentJson, "limit", true, -1, num -> num > 0);

            return new DistributionComponent(component, chance, limit);
        }

        @Override
        public final AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn) {

            Dimension size = new Dimension(width.get(random.next()), length.get(random.next()));
            AreaGrid area = new AreaGrid(size, spawn);

            int rollCount = rolls.get(random.next());

            iterationType.generate(components, rollCount,
                    component -> component.component().generate(area, random, location), DistributionComponent::chance,
                    component -> component.limit().get(random.next()), random, 0.5, 5);

            return area;
        }
    }

    public final class GridGenerationLayout extends GenerationLayout {

        private final DiscreteRange gridWidth;

        private final DiscreteRange gridLength;

        private final DiscreteRange cellWidth;

        private final DiscreteRange cellLength;

        private final DiscreteRange borderWidth;

        private final DiscreteRange rolls;

        private final SelectionType iterationType;

        private final ImmutableList<GridComponent> components;

        public GridGenerationLayout(TracedDictionary json) throws MissingPropertyException, PropertyTypeException,
                UnexpectedValueException, CollectionSizeException, NumberBoundsException, LoggedException {

            super(json);

            gridWidth = DiscreteRange.createRange(json, "gridWidth", false, -1, num -> num > 0);
            gridLength = DiscreteRange.createRange(json, "gridLength", false, -1, num -> num > 0);
            cellWidth = DiscreteRange.createRange(json, "cellWidth", false, -1, num -> num > 0);
            cellLength = DiscreteRange.createRange(json, "cellLength", false, -1, num -> num > 0);

            borderWidth = DiscreteRange.createRange(json, "borderWidth", true, 0, num -> num >= 0);

            rolls = DiscreteRange.createRange(json, "rolls", false, -1, num -> num > 0);
            iterationType = createSelectionType(json, "iterationType");

            ArrayList<GridComponent> componentsList = new ArrayList<>();

            TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
            TracedArray componentsJson = componentsEntry.getValue();
            for (int i : componentsJson) {

                TracedEntry<TracedDictionary> componentEntry = componentsJson.getAsDict(i, false);
                TracedDictionary componentJson = componentEntry.getValue();

                componentsList.add(createGridComponent(componentJson));
            }
            components = new ImmutableList<>(componentsList);
        }

        private final record GridComponent(GenerationLayout component, DiscreteRange width, DiscreteRange length,
                ImmutableSet<String> tag, WeightedRoll<GenerationPlacement> placement, DiscreteRange count,
                double chance, DiscreteRange limit) {

            public final Dimension getSize(DeterministicRandom random) {

                return new Dimension(width.get(random.next()), length.get(random.next()));
            }

            public final AreaGrid generateContent(Dimension size, Dimension cellSize, int borderWidth,
                    DeterministicRandom random, GlobalLocationInstance location) {

                AreaGrid area = new AreaGrid(new Dimension(size.width * (cellSize.width + borderWidth) - borderWidth,
                        size.height * (cellSize.height + borderWidth) - borderWidth), null);
                component.generate(area, random, location);

                return area;
            }
        }

        private GridComponent createGridComponent(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedDictionary> componentEntry = json.getAsDict("component", false);
            TracedDictionary componentJson = componentEntry.getValue();

            GenerationLayout component = createGenerationLayout(componentJson);

            DiscreteRange width = DiscreteRange.createRange(json, "width", true, 1, num -> num > 0);
            DiscreteRange length = DiscreteRange.createRange(json, "length", true, 1, num -> num > 0);

            WeightedRoll<GenerationPlacement> placement = GenerationPlacement.createPlacement(json, "placement");

            ImmutableSet<String> tag = json.get("tag",
                    List.of(json.stringCase(entry -> new ImmutableSet<>(entry.getValue())), json.arrayCase(entry -> {

                        HashSet<String> tagSet = new HashSet<>();

                        TracedArray tagJson = entry.getValue();
                        for (int i : tagJson) {

                            TracedEntry<String> tagEntry = tagJson.getAsString(i, false, null);
                            tagSet.add(tagEntry.getValue());
                        }

                        return new ImmutableSet<>(tagSet);
                    }), json.nullCase(_ -> null)));

            DiscreteRange count = DiscreteRange.createRange(json, "count", true, 1, num -> num > 0);

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
            double chance = chanceEntry.getValue();

            DiscreteRange limit = DiscreteRange.createRange(componentJson, "limit", true, -1, num -> num > 0);

            return new GridComponent(component, width, length, tag, placement, count, chance, limit);
        }

        private final class GridPlacementArea implements PlacementArea {

            private final Rectangle gridBounds;

            @Override
            public final int getWidth() {

                return gridBounds.width;
            }

            @Override
            public final int getLength() {

                return gridBounds.height;
            }

            private final Dimension cellSize;

            private final int borderWidth;

            private final AreaGrid area;

            private final boolean[] cells;

            private final ArrayList<Rectangle> components = new ArrayList<>();

            private final HashMap<String, ArrayList<Rectangle>> tagged = new HashMap<>();

            @Override
            public final List<Rectangle> getMatches(String tag) {

                return tagged.getOrDefault(tag, new ArrayList<>());
            }

            public final boolean add(GridComponent component, DeterministicRandom random,
                    GlobalLocationInstance location) {

                Dimension componentSize = component.getSize(random);

                GenerationPlacement placement = component.placement.get(random.next());
                GenerationShapeInstance placementShape = placement.generateShape(this, componentSize.width,
                        componentSize.height, random);
                Point point = placementShape.getPoint(p -> {

                    Rectangle bounds = new Rectangle(p, componentSize);
                    if (!gridBounds.contains(bounds)) {

                        return false;
                    }

                    if (bounds.width * bounds.height > components.size()) {

                        for (Rectangle rect : components) {

                            if (rect.intersects(bounds)) {

                                return false;
                            }
                        }

                        return true;
                    }

                    for (int j = 0; j < bounds.width * bounds.height; j++) {

                        int x = bounds.x + j % bounds.width;
                        int z = bounds.y + j / bounds.width;

                        if (cells[x + z * gridBounds.width]) {

                            return false;
                        }
                    }

                    return true;
                }, random);
                if (point == null) {

                    return false;
                }

                Rectangle bounds = new Rectangle(point, componentSize);
                components.add(bounds);

                for (int j = 0; j < bounds.width * bounds.height; j++) {

                    int x = bounds.x + j % bounds.width;
                    int z = bounds.y + j / bounds.width;

                    cells[x + z * gridBounds.width] = true;
                }

                AreaGrid componentArea = component.generateContent(componentSize, cellSize, borderWidth, random,
                        location);

                int x = componentSize.width * (cellSize.width + borderWidth);
                int z = componentSize.height * (cellSize.height + borderWidth);

                area.addArea(componentArea, x, z);

                if (component.tag != null) {

                    for (String tag : component.tag) {

                        tagged.computeIfAbsent(tag, _ -> new ArrayList<>()).add(bounds);
                    }
                }

                return true;
            }

            public GridPlacementArea(Dimension gridSize, Dimension cellSize, int borderWidth,
                    GenerationPlacement spawn) {

                this.gridBounds = new Rectangle(gridSize);

                cells = new boolean[gridSize.width * gridSize.height];

                this.cellSize = cellSize;
                this.borderWidth = borderWidth;

                area = new AreaGrid(new Dimension(gridBounds.width * (cellSize.width + borderWidth) - borderWidth,
                        gridBounds.height * (cellSize.height + borderWidth) - borderWidth), spawn);
            }
        }

        @Override
        public final AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn) {

            Dimension gridSize = new Dimension(gridWidth.get(random.next()), gridLength.get(random.next()));
            Dimension cellSize = new Dimension(cellWidth.get(random.next()), cellLength.get(random.next()));

            GridPlacementArea area = new GridPlacementArea(gridSize, cellSize, borderWidth.get(random.next()), spawn);

            int rollCount = rolls.get(random.next());

            iterationType.generate(components, rollCount,
                    SelectionType.apply(component -> area.add(component, random, location),
                            component -> component.count().get(random.next())),
                    GridComponent::chance, component -> component.limit().get(random.next()), random, 0.5, 5);

            return area.area;
        }
    }

    public final class BlueprintGenerationLayout extends GenerationLayout {

        public BlueprintGenerationLayout(TracedDictionary json) throws LoggedException {

            super(json);
        }

        @Override
        public final AreaGrid generateContent(AreaGrid parent, DeterministicRandom random,
                GlobalLocationInstance location, GenerationPlacement spawn) {

            return null;
        }
    }
}
