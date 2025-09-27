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

import com.transcendruins.assets.AssetType;
import static com.transcendruins.assets.AssetType.ELEMENT;
import static com.transcendruins.assets.AssetType.ENTITY;
import static com.transcendruins.assets.AssetType.LAYOUT;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.elements.ElementContext;
import com.transcendruins.assets.entities.EntityContext;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.layouts.LayoutAttributes.AssetGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.DistributionGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GridGenerationSchema;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>LayoutInstance</code>: A class representing a generated layout
 * instance.
 */
public final class LayoutInstance extends AssetInstance {

    private GenerationInstance generation;

    public final int getWidth() {

        return generation.getWidth();
    }

    public final int getLength() {

        return generation.getLength();
    }

    /**
     * Creates a new instance of the <code>LayoutInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>LayoutInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public LayoutInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        LayoutContext context = (LayoutContext) assetContext;
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        LayoutAttributes attributes = (LayoutAttributes) attributeSet;

        generation = calculateAttribute(attributes.getGeneration(), this::createGeneration, generation);
    }

    public GenerationInstance createGeneration(GenerationSchema schema) {

        // TODO implement rest of generation types
        return switch (schema) {

        case AssetGenerationSchema s -> new AssetGenerationInstance(s);

        // case LayoutGenerationSchema s -> new LayoutGenerationInstance(s);

        case DistributionGenerationSchema s -> new DistributionGenerationInstance(s);

        case GridGenerationSchema s -> new GridGenerationInstance(s);

        // case BlueprintGenerationSchema s -> new BlueprintGenerationInstance(s);

        default -> null;
        };
    }

    public abstract class GenerationInstance {

        private final String componentId;

        public final String getComponentId() {

            return componentId;
        }

        private final ImmutableList<String> componentTags;

        public final ImmutableList<String> getComponentTags() {

            return componentTags;
        }

        private final Range count;

        public final Range getCount() {

            return count;
        }

        public GenerationInstance(LayoutAttributes.GenerationSchema schema) {

            componentId = schema.getComponentId();
            componentTags = schema.getComponentTags();

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

        public final AssetInstance getAsset() {

            return asset;
        }

        public AssetGenerationInstance(AssetGenerationSchema schema) {

            super(schema);

            AssetPresets presets = schema.getAsset();
            AssetType type = presets.getType();

            World world = getWorld();

            asset = switch (type) {

            case ELEMENT -> {

                AssetContext context = new ElementContext(presets, world, null);
                yield ELEMENT.createAsset(context);
            }

            case ENTITY -> {

                AssetContext context = new EntityContext(presets, world, null);
                yield ENTITY.createAsset(context);
            }

            case LAYOUT -> {

                AssetContext context = new LayoutContext(presets, world, null);
                yield LAYOUT.createAsset(context);
            }

            case null, default -> null;
            };
        }

        @Override
        public final int getWidth() {

            return switch (asset) {

            case PrimaryAssetInstance primary -> primary.getTileWidth();

            case LayoutInstance layout -> layout.getWidth();

            default -> 0;
            };
        }

        @Override
        public final int getLength() {

            return switch (asset) {

            case PrimaryAssetInstance primary -> primary.getTileLength();

            case LayoutInstance layout -> layout.getLength();

            default -> 0;
            };
        }

        @Override
        public final AreaGrid generate() {

            return switch (asset) {

            case PrimaryAssetInstance primary -> {

                AreaGrid area = createArea();
                // area.apply(primary);
                yield area;
            }

            case LayoutInstance layout -> {

                AreaGrid area = createArea();
                // area.apply(layout);
                yield area;
            }

            default -> null;
            };
        }
    }

    public final class DistributionGenerationInstance extends GenerationInstance {

        public DistributionGenerationInstance(DistributionGenerationSchema schema) {

            super(schema);
        }

        @Override
        public final int getWidth() {

            return 0;
        }

        @Override
        public final int getLength() {

            return 0;
        }

        @Override
        public final AreaGrid generate() {

            return createArea();
        }
    }

    public final class GridGenerationInstance extends GenerationInstance {

        public GridGenerationInstance(GridGenerationSchema schema) {

            super(schema);

            LayoutDimension cellSize = schema.getCellDimensions();
            int cellWidth = cellSize.getWidth();
            int cellLength = cellSize.getLength();

            WeightedRoll<GenerationSchema> componentSchemas = schema.getComponents();
            schema.getGridSize();
            schema.getIterationType();
            schema.getPlacement();
        }

        @Override
        public final int getWidth() {

            return 0;
        }

        @Override
        public final int getLength() {

            return 0;
        }

        @Override
        public final AreaGrid generate() {

            return createArea();
        }
    }

    public final AreaGrid generate() {

        return generation.generate();
    }

    @Override
    protected final void onUpdate(double time) {
    }
}
