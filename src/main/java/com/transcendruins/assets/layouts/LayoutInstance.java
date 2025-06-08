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

import static com.transcendruins.assets.AssetType.ELEMENT;
import static com.transcendruins.assets.AssetType.ENTITY;
import static com.transcendruins.assets.AssetType.LAYOUT;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.layouts.LayoutAttributes.AssetGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.LayoutGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.DistributionGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GridGenerationSchema;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.primaryassets.elements.ElementContext;
import com.transcendruins.assets.primaryassets.elements.ElementInstance;
import com.transcendruins.assets.primaryassets.entities.EntityContext;
import com.transcendruins.assets.primaryassets.entities.EntityInstance;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>LayoutInstance</code>: A class representing a generated layout
 * instance.
 */
public final class LayoutInstance extends AssetInstance {

    private GenerationInstance generation;

    public int getWidth() {

        return generation.getWidth();
    }

    public int getLength() {

        return generation.getLength();
    }

    /**
     * Creates a new instance of the <code>LayoutInstance</code> class.
     * 
     * @param context <code>LayoutContext</code>: The context used to generate this
     *                <code>LayoutInstance</code> instance.
     */
    public LayoutInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LayoutContext context = (LayoutContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        LayoutAttributes attributes = (LayoutAttributes) attributeSet;

        generation = calculateAttribute(attributes.getGeneration(), this::createGeneration, generation);
    }

    public GenerationInstance createGeneration(GenerationSchema schema) {

        return switch (schema) {

        case AssetGenerationSchema s -> new AssetGenerationInstance(s);

        case LayoutGenerationSchema s -> new LayoutGenerationInstance(s);

        case DistributionGenerationSchema s -> new DistributionGenerationInstance(s);

        case GridGenerationSchema s -> new GridGenerationInstance(s);

        case BlueprintGenerationSchema s -> new BlueprintGenerationInstance(s);

        default -> null;
        };
    }

    public abstract class GenerationInstance {

        private final String componentId;

        public String getComponentId() {

            return componentId;
        }

        private final ImmutableList<String> componentTags;

        public ImmutableList<String> getComponentTags() {

            return componentTags;
        }

        private final double chance;

        public double getChance() {

            return chance;
        }

        private final Range count;

        public Range getCount() {

            return count;
        }

        public GenerationInstance(LayoutAttributes.GenerationSchema schema) {

            componentId = schema.getComponentId();
            componentTags = schema.getComponentTags();

            chance = schema.getChance();
            count = schema.getCount();
        }

        public abstract int getWidth();

        public abstract int getLength();

        protected final AreaGrid createArea() {

            return new AreaGrid(getWidth(), getLength());
        }

        public abstract AreaGrid generate();
    }

    public final class AssetGenerationInstance extends GenerationInstance {

        private final AssetInstance asset;

        private final LayoutAttributes.GenerationType type;

        public AssetGenerationInstance(AssetGenerationSchema schema) {

            super(schema);
            type = schema.getType();

            World world = getWorld();

            AssetPresets presets = schema.getPresets();
            AssetContext context;
            asset = switch (type) {

            case ELEMENT -> {

                context = new ElementContext(presets, world, null);
                yield ELEMENT.createAsset(context);
            }

            case ENTITY -> {

                context = new EntityContext(presets, world, null);
                yield ENTITY.createAsset(context);
            }

            case LAYOUT -> {

                context = new LayoutContext(presets, world, null);
                yield LAYOUT.createAsset(context);
            }

            case null, default -> null;
            };
        }

        @Override
        public int getWidth() {

            return switch (type) {

            case ELEMENT, ENTITY -> {

                PrimaryAssetInstance primary = (PrimaryAssetInstance) asset;
                yield primary.getTileWidth();
            }

            case LAYOUT -> {

                LayoutInstance layout = (LayoutInstance) asset;
                yield layout.getWidth();
            }

            case null, default -> 0;
            };
        }

        @Override
        public int getLength() {

            return switch (type) {

            case ELEMENT, ENTITY -> {

                PrimaryAssetInstance primary = (PrimaryAssetInstance) asset;
                yield primary.getTileLength();
            }

            case LAYOUT -> {

                LayoutInstance layout = (LayoutInstance) asset;
                yield layout.getLength();
            }

            case null, default -> 0;
            };
        }

        @Override
        public AreaGrid generate() {

            return switch (type) {

            case ELEMENT -> {

                AreaGrid area = createArea();

                ElementInstance element = (ElementInstance) asset;
                area.apply(element);

                yield area;
            }

            case ENTITY -> {

                AreaGrid area = createArea();

                EntityInstance entity = (EntityInstance) asset;
                area.apply(entity);

                yield area;
            }

            case LAYOUT -> {

                LayoutInstance layout = (LayoutInstance) asset;
                yield layout.generate();
            }

            case null, default -> null;
            };
        }
    }

    public final class DistributionGenerationInstance extends GenerationInstance {

        public DistributionGenerationInstance(DistributionGenerationSchema schema) {

            super(schema);
        }

        @Override
        public int getWidth() {

        }

        @Override
        public int getLength() {

        }

        @Override
        public AreaGrid generate() {

        }
    }

    public final class GridGenerationInstance extends GenerationInstance {

        public GridGenerationInstance(GridGenerationSchema schema) {

            super(schema);

            LayoutDimension cellSize = schema.getCellDimensions();
            int cellWidth = cellSize.getWidth();
            int cellLength = cellSize.getLength();

            ImmutableList<GenerationSchema> componentSchemas = schema.getComponents();
            schema.getGridSize();
            schema.getIterationType();
            schema.getPlacement();
        }

        @Override
        public int getWidth() {

        }

        @Override
        public int getLength() {

        }

        @Override
        public AreaGrid generate() {

        }
    }

    public final class BlueprintGenerationInstance extends GenerationInstance {

        private final int width;

        @Override
        public int getWidth() {

            return width;
        }

        private final int length;

        @Override
        public int getLength() {

            return length;
        }

        public BlueprintGenerationInstance(BlueprintGenerationSchema schema) {

            super(schema);

            width = schema.getWidth();
            length = schema.getLength();
        }

        @Override
        public AreaGrid generate() {

        }
    }

    public AreaGrid generate() {

        return generation.generate();
    }

    @Override
    protected void onUpdate(double time) {
    }
}
