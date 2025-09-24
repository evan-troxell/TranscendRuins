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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.transcendruins.assets.AssetType.ELEMENT;
import static com.transcendruins.assets.AssetType.ENTITY;
import static com.transcendruins.assets.AssetType.LAYOUT;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.SelectionType;
import static com.transcendruins.assets.SelectionType.parseSelectionType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.layouts.shape.GenerationShapeSchema;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.world.World;

/**
 * <code>LayoutAttributes</code>: A class which represents the attributes of a
 * <code>LayoutSchema</code> instance.
 */
public final class LayoutAttributes extends AssetAttributes {

    private final ImmutableMap<AssetType, ImmutableMap<String, AssetPresets>> definitions;

    private final GenerationSchema generation;

    public GenerationSchema getGeneration() {

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

        // The definitions and generation should only be defined once.
        if (isBase) {

            HashMap<AssetType, ImmutableMap<String, AssetPresets>> definitionsMap = new HashMap<>();

            HashMap<String, AssetPresets> layouts = new HashMap<>();
            HashMap<String, AssetPresets> elements = new HashMap<>();
            HashMap<String, AssetPresets> entities = new HashMap<>();

            TracedEntry<TracedDictionary> definitionsEntry = json.getAsDict("definitions", false);
            TracedDictionary definitionsJson = definitionsEntry.getValue();

            TracedEntry<TracedDictionary> layoutsEntry = definitionsJson.getAsDict("layouts", true);
            if (layoutsEntry.containsValue()) {

                TracedDictionary layoutsJson = layoutsEntry.getValue();
                for (String key : layoutsJson) {

                    TracedEntry<AssetPresets> layoutEntry = layoutsJson.getAsPresets(key, false, LAYOUT);
                    AssetPresets layout = layoutEntry.getValue();
                    addAssetDependency(layout);

                    layouts.put(key, layout);
                }
            }

            TracedEntry<TracedDictionary> elementsEntry = definitionsJson.getAsDict("elements", true);
            if (elementsEntry.containsValue()) {

                TracedDictionary elementsJson = elementsEntry.getValue();
                for (String key : elementsJson) {

                    TracedEntry<AssetPresets> elementEntry = elementsJson.getAsPresets(key, false, ELEMENT);
                    AssetPresets element = elementEntry.getValue();
                    addAssetDependency(element);

                    elements.put(key, element);
                }
            }

            TracedEntry<TracedDictionary> entitiesEntries = definitionsJson.getAsDict("entities", true);
            if (entitiesEntries.containsValue()) {

                TracedDictionary entitiesJson = entitiesEntries.getValue();
                for (String key : entitiesJson) {

                    TracedEntry<AssetPresets> entityEntry = entitiesJson.getAsPresets(key, false, ENTITY);
                    AssetPresets entity = entityEntry.getValue();
                    addAssetDependency(entity);

                    entities.put(key, entity);
                }
            }

            definitionsMap.put(LAYOUT, new ImmutableMap<>(layouts));
            definitionsMap.put(ELEMENT, new ImmutableMap<>(elements));
            definitionsMap.put(ENTITY, new ImmutableMap<>(entities));
            definitions = new ImmutableMap<>(definitionsMap);

            generation = createGeneration(json);
        } else {

            definitions = null;
            generation = null;
        }
    }

    public GenerationSchema createGeneration(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case "element" -> new AssetGenerationSchema(json, ELEMENT);

        case "entity" -> new AssetGenerationSchema(json, ENTITY);

        case "layout" -> new LayoutGenerationSchema(json);

        case "distributed" -> new DistributionGenerationSchema(json);

        case "grid" -> new GridGenerationSchema(json);

        case "blueprint" -> new BlueprintGenerationSchema(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    /**
     * <code>GenerationSchema</code>: A class representing any generation schema.
     */
    public abstract class GenerationSchema {

        /**
         * <code>String</code>: The component ID of this <code>GenerationSchema</code>
         * instance.
         */
        private final String componentId;

        /**
         * Retrieves the component ID of this <code>GenerationSchema</code> instance.
         * 
         * @return <code>String</code>: The <code>componentId</code> field of this
         *         <code>GenerationSchema</code> instance.
         */
        public String getComponentId() {

            return componentId;
        }

        /**
         * <code>ImmutableList&lt;String&gt;</code>: The component tags of this
         * <code>GenerationSchema</code> instance.
         */
        private final ImmutableList<String> componentTags;

        /**
         * Retrieves the component tags of this <code>GenerationSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;String&gt;</code>: The
         *         <code>componentTags</code> field of this
         *         <code>GenerationSchema</code> instance.
         */
        public ImmutableList<String> getComponentTags() {

            return componentTags;
        }

        /**
         * <code>Range</code>: The count of this <code>GenerationSchema</code> instance.
         * This value represents the range of how many times this
         * <code>GenerationSchema</code> instance can be selected in a limited
         * selection, but a fixed count in a full selection.
         */
        private final Range count;

        /**
         * Retrieves the count of this <code>GenerationSchema</code> instance.
         * 
         * @return <code>Range</code>: The <code>count</code> field of this
         *         <code>GenerationSchema</code> instance.
         */
        public Range getCount() {

            return count;
        }

        private final WeightedRoll<GenerationPlacementSchema> placement;

        public WeightedRoll<GenerationPlacementSchema> getPlacement() {

            return placement;
        }

        public GenerationSchema(TracedDictionary json) throws LoggedException {

            count = Range.createRange(json, "count", true, num -> num >= 1);

            placement = GenerationPlacementSchema.createPlacement(json, "placement");

            TracedEntry<String> componentIdEntry = json.getAsString("componentId", true, null);
            if (componentIdEntry.containsValue()) {

                componentId = componentIdEntry.getValue();
            } else {

                componentId = null;
            }

            TracedEntry<TracedArray> componentTagsEntry = json.getAsArray("componentTags", true);
            if (componentTagsEntry.containsValue()) {

                TracedArray componentTagsJson = componentTagsEntry.getValue();
                ArrayList<String> componentTagsList = new ArrayList<>(componentTagsJson.size());

                for (int i : componentTagsJson) {

                    TracedEntry<String> tagEntry = componentTagsJson.getAsString(i, false, null);
                    componentTagsList.add(tagEntry.getValue());
                }

                componentTags = new ImmutableList<>(componentTagsList);
            } else {

                componentTags = new ImmutableList<>();
            }
        }
    }

    public final class AssetGenerationSchema extends GenerationSchema {

        private final AssetType type;

        public AssetType getType() {

            return type;
        }

        private final AssetPresets asset;

        public AssetPresets getAsset() {

            return asset;
        }

        private final Vector tileOffset;

        public Vector getTileOffset() {

            return tileOffset;
        }

        public AssetGenerationSchema(TracedDictionary json, AssetType type) throws LoggedException {

            super(json);

            this.type = type;

            String key = type.toString().toLowerCase();

            // Attempt to build the asset presets from the definitions.
            AssetPresets existing = json.get(key, List.of(

                    json.stringCase(entry -> definitions.get(type).get(entry.getValue())),
                    json.defaultCase(_ -> null)));

            // If the asset presets are not found in the definitions, create them from the
            // JSON definitions.
            if (existing == null) {

                TracedEntry<AssetPresets> assetEntry = json.getAsPresets(key, false, type);
                asset = assetEntry.getValue();
                addAssetDependency(asset);
            } else {

                asset = existing;
            }

            // Creates the boundaries to constrain the tile offset to a 1-tile square.
            Vector upperBounds = new Vector(1, Integer.MAX_VALUE, 1);
            Vector lowerBounds = new Vector(-1, Integer.MIN_VALUE, -1);

            TracedEntry<Vector> tileOffsetEntry = json.getAsVector("tileOffset", true, 3, lowerBounds, upperBounds);
            tileOffset = tileOffsetEntry.containsValue() ? tileOffsetEntry.getValue() : Vector.IDENTITY_VECTOR;
        }
    }

    public final class LayoutGenerationSchema extends GenerationSchema {

        private final AssetPresets layout;

        public AssetPresets getLayout() {

            return layout;
        }

        public LayoutGenerationSchema(TracedDictionary json) throws LoggedException {

            super(json);

            // Attempt to build the asset presets from the definitions.
            AssetPresets existing = json.get("layout", List.of(

                    json.stringCase(entry -> definitions.get(LAYOUT).get(entry.getValue())),
                    json.defaultCase(_ -> null)));

            // If the asset presets are not found in the definitions, create them from the
            // JSON definitions.

            if (existing == null) {

                TracedEntry<AssetPresets> layoutEntry = json.getAsPresets("layout", false, LAYOUT);
                layout = layoutEntry.getValue();
                addAssetDependency(layout);
            } else {

                layout = existing;
            }
        }
    }

    public final class DistributionGenerationSchema extends GenerationSchema {

        private final WeightedRoll<GenerationShapeSchema> size;

        public WeightedRoll<GenerationShapeSchema> getSize() {

            return size;
        }

        private final SelectionType iterationType;

        public SelectionType getIterationType() {

            return iterationType;
        }

        private final WeightedRoll<GenerationSchema> components;

        public WeightedRoll<GenerationSchema> getComponents() {

            return components;
        }

        public DistributionGenerationSchema(TracedDictionary json) throws LoggedException {

            super(json);

            size = GenerationShapeSchema.createShape(json, "size");

            iterationType = parseSelectionType(json, "iterationType");

            components = json.getAsRoll("components", false, null, entry -> createGeneration(entry.getValue()));
        }
    }

    public final class GridGenerationSchema extends GenerationSchema {

        private final GenerationShapeSchema gridSize;

        public GenerationShapeSchema getGridSize() {

            return gridSize;
        }

        private final LayoutDimension cellDimensions;

        public LayoutDimension getCellDimensions() {

            return cellDimensions;
        }

        private final SelectionType iterationType;

        public SelectionType getIterationType() {

            return iterationType;
        }

        private final WeightedRoll<GenerationSchema> components;

        public WeightedRoll<GenerationSchema> getComponents() {

            return components;
        }

        public GridGenerationSchema(TracedDictionary json) throws MissingPropertyException, PropertyTypeException,
                UnexpectedValueException, CollectionSizeException, NumberBoundsException, LoggedException {

            super(json);

            cellDimensions = new LayoutDimension(json, "cellDimensions", false);

            iterationType = parseSelectionType(json, "iterationType");

            components =

                    json.getAsRoll("components", entry -> createGeneration(entry.getValue()));
        }
    }

    /**
     * Parses a value from a collection into a generation placement schema.
     * 
     * @param json <code>TracedCollection</code>: The collection to parse.
     * @param key  <code>Object</code>: The key from the collection to parse.
     * @return <code>WeightedRoll&lt;GenerationPlacementSchema&gt;</code>: The
     *         resulting set of available generation placements.
     * @throws LoggedException Thrown if an error is raised while processing the
     *                         collection.
     */
    public WeightedRoll<GenerationPlacementSchema> createPlacement(TracedCollection json, Object key)
            throws LoggedException {

        return json.getAsRoll(key, true, GenerationPlacementSchema.DEFAULT,
                entry -> new GenerationPlacementSchema(entry.getValue()));
    }

    public static final class GenerationPlacementSchema {

        public static final GenerationPlacementSchema DEFAULT = new GenerationPlacementSchema();

        private GenerationPlacementSchema() {

            shape = GenerationShapeSchema.DEFAULT;
        }

        private final WeightedRoll<GenerationShapeSchema> shape;

        public WeightedRoll<GenerationShapeSchema> getShape(World world) {

            return shape;
        }

        private final GenerationPlacementDistributionSchema distribution;

        public GenerationPlacementDistributionSchema getDistribution() {

            return distribution;
        }

        private final WeightedRoll<GenerationPlacementCenterSchema> center;

        public WeightedRoll<GenerationPlacementCenterSchema> getCenter() {

            return center;
        }

        private GenerationPlacementSchema(TracedDictionary json) throws LoggedException {

            shape = GenerationShapeSchema.createShape(json, "shape");

            distribution = createDistribution(json, "distribution");

            center = createCenter(json, "center");
        }

        public final WeightedRoll<GenerationPlacementCenterSchema> createCenter(TracedCollection collection, Object key)
                throws LoggedException {

            return collection.getAsRoll(key, true, GenerationPlacementCenterSchema.DEFAULT,
                    entry -> createCenter(entry.getValue()));
        }

        private final GenerationPlacementCenterSchema createCenter(TracedDictionary dict) throws LoggedException {

            TracedEntry<String> typeEntry = dict.getAsString("type", false, null);
            String type = typeEntry.getValue();

            return switch (type) {

            case "element", "entity", "layout" -> new AssetCenterSchema(dict, type);

            case "id", "tag" -> new SelecterCenterSchema(dict, type);

            case "coordinate", "relative" -> new PositionCenterSchema(dict, type);

            default -> throw new UnexpectedValueException(typeEntry);

            };
        }

        public static abstract class GenerationPlacementCenterSchema {

            public static final GenerationPlacementCenterSchema DEFAULT = new GenerationPlacementCenterSchema() {
            };

            private GenerationPlacementCenterSchema() {

            }

            public GenerationPlacementCenterSchema(TracedDictionary dict) throws LoggedException {
            }
        }

    }
}
