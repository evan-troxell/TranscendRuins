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
import java.util.Set;

import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.modelassets.elements.ElementPresets;
import com.transcendruins.assets.modelassets.entities.EntityPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.NumberBoundsException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LayoutAttributes</code>: A class which represents the
 * attributes of a <code>LayoutSchema</code> instance.
 */
public final class LayoutAttributes extends AssetAttributes {

    public enum Generation {

        LAYOUT("layout"),
        ELEMENT("element"),
        ENTITY("entity"),

        DISTRIBUTION("distributed"),
        GRID("grid");

        private final Set<String> keys;

        private Generation(String... keys) {

            this.keys = Set.of(keys);
        }

        public static final Generation get(TracedEntry<String> entry) throws UnexpectedValueException {

            String key = entry.getValue();

            for (Generation type : values()) {

                if (type.keys.contains(key)) {

                    return type;
                }
            }

            throw new UnexpectedValueException(entry);
        }
    }

    private final LayoutDimension size;

    public LayoutDimension getSize() {

        return size;
    }

    private final ImmutableMap<Generation, ImmutableMap<String, AssetPresets>> definitions;

    private final GenerationSchema generation;

    public GenerationSchema getGeneration() {

        return generation;
    }

    /**
     * Compiles this <code>LayoutAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created
     *               this <code>LayoutAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>LayoutAttributes</code>
     *               instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>LayoutAttributes</code> instance is the
     *               base attribute set of a
     *               <code>LayoutAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>LayoutAttributes</code>
     *                         instance.
     */
    public LayoutAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        size = new LayoutDimension(json, "size", false);

        HashMap<Generation, ImmutableMap<String, AssetPresets>> definitionsMap = new HashMap<>();

        HashMap<String, AssetPresets> layouts = new HashMap<>();
        HashMap<String, AssetPresets> elements = new HashMap<>();
        HashMap<String, AssetPresets> entities = new HashMap<>();

        TracedEntry<TracedDictionary> definitionsEntry = json.getAsDict("definitions", true);
        if (definitionsEntry.containsValue()) {

            TracedDictionary definitionsJson = definitionsEntry.getValue();

            TracedEntry<TracedDictionary> layoutsEntry = definitionsJson.getAsDict("layouts", true);
            if (layoutsEntry.containsValue()) {

                TracedDictionary layoutsJson = layoutsEntry.getValue();
                for (String key : layoutsJson.getKeys()) {

                    LayoutPresets layout = LayoutPresets.createPresets(layoutsJson, key, false);
                    addAssetDependency(layout);

                    layouts.put(key, layout);
                }
            }

            TracedEntry<TracedDictionary> elementsEntry = definitionsJson.getAsDict("elements", true);
            if (elementsEntry.containsValue()) {

                TracedDictionary elementsJson = elementsEntry.getValue();
                for (String key : elementsJson.getKeys()) {

                    ElementPresets element = ElementPresets.createPresets(elementsJson, key, false);
                    addAssetDependency(element);

                    elements.put(key, element);
                }
            }

            TracedEntry<TracedDictionary> entitiesEntries = definitionsJson.getAsDict("entities", true);
            if (entitiesEntries.containsValue()) {

                TracedDictionary entitiesJson = entitiesEntries.getValue();
                for (String key : entitiesJson.getKeys()) {

                    EntityPresets entity = EntityPresets.createPresets(entitiesJson, key, false);
                    addAssetDependency(entity);

                    entities.put(key, entity);
                }
            }
        }

        definitionsMap.put(Generation.LAYOUT, new ImmutableMap<>(layouts));
        definitionsMap.put(Generation.ELEMENT, new ImmutableMap<>(elements));
        definitionsMap.put(Generation.ENTITY, new ImmutableMap<>(entities));
        definitions = new ImmutableMap<>(definitionsMap);

        generation = createGeneration(json, !isBase);
    }

    public final GenerationSchema createGeneration(TracedDictionary json, boolean nullCaseAllowed)
            throws LoggedException {

        TracedEntry<String> generationEntry = json.getAsString("generation", nullCaseAllowed, null);

        if (!generationEntry.containsValue()) {

            return null;
        }

        Generation type = Generation.get(generationEntry);

        return switch (type) {

            case LAYOUT, ELEMENT, ENTITY -> new AssetGenerationSchema(json, type);

            case DISTRIBUTION -> new DistributionGenerationSchema(json);
            case GRID -> new GridGenerationSchema(json);

            default -> null;
        };
    }

    /**
     * <code>LayoutAttributes.GenerationSchema</code>: A subclass representing
     * any generation schema.
     */
    public abstract class GenerationSchema {

        private final double chance;

        public double getChance() {

            return chance;
        }

        private final Range count;

        public Range getCount() {

            return count;
        }

        private final GenerationPlacement placement;

        public GenerationPlacement getPlacement() {

            return placement;
        }

        public GenerationSchema(TracedDictionary json)
                throws LoggedException {

            TracedEntry<Double> chanceEntry = json.getAsDouble("chance", true, 100.0, num -> num > 0);
            chance = chanceEntry.getValue();

            count = Range.createRange(json, "count", true, true, num -> num >= 1);

            placement = new GenerationPlacement(json, "placement");
        }
    }

    public final class AssetGenerationSchema extends GenerationSchema {

        private final AssetPresets presets;

        public AssetPresets getPresets() {

            return presets;
        }

        public AssetGenerationSchema(TracedDictionary json, Generation type) throws LoggedException {

            super(json);

            String key = type.toString().toLowerCase();

            AssetPresets existing = json.get(key, List.of(

                    json.stringCase(entry -> definitions.get(type).get(entry.getValue())),
                    json.defaultCase(_ -> null)));

            if (existing == null) {

                presets = switch (type) {

                    case LAYOUT -> LayoutPresets.createPresets(json, key, false);

                    case ELEMENT -> ElementPresets.createPresets(json, key, false);

                    case ENTITY -> EntityPresets.createPresets(json, key, false);

                    default -> null;
                };
                addAssetDependency(presets);
            } else {

                presets = existing;
            }
        }
    }

    public final class DistributionGenerationSchema extends GenerationSchema {

        private final boolean collectionIteration;

        public boolean getCollectionIteration() {

            return collectionIteration;
        }

        private final ImmutableList<GenerationSchema> components;

        public DistributionGenerationSchema(TracedDictionary json) throws LoggedException {

            super(json);

            TracedEntry<String> iterationTypeEntry = json.getAsString("iterationType", false, null);
            collectionIteration = switch (iterationTypeEntry.getValue()) {

                case "select", "selection" -> false;

                case "collect", "collection" -> true;

                default -> throw new UnexpectedValueException(iterationTypeEntry);
            };

            TracedEntry<TracedArray> componentsEntry = json.getAsArray("components", false);
            TracedArray componentsJson = componentsEntry.getValue();

            if (componentsJson.isEmpty()) {

                throw new CollectionSizeException(componentsEntry, componentsJson);
            }

            ArrayList<GenerationSchema> componentsList = new ArrayList<>(componentsJson.size());

            for (int i : componentsJson.getIndices()) {

                TracedEntry<TracedDictionary> componentEntry = componentsJson.getAsDict(i, false);
                componentsList.add(createGeneration(componentEntry.getValue(), false));
            }

            components = new ImmutableList<>(componentsList);
        }
    }

    public final class GridGenerationSchema extends GenerationSchema {

        private final LayoutDimension gridSize;

        private final LayoutDimension cellSize;

        public GridGenerationSchema(TracedDictionary json) throws MissingPropertyException, PropertyTypeException,
                UnexpectedValueException, CollectionSizeException, NumberBoundsException, LoggedException {

            super(json);

            gridSize = new LayoutDimension(json, "gridSize", true);

            cellSize = new LayoutDimension(json, "cellSize", false);
        }
    }
}
