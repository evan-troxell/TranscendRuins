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
import java.awt.Rectangle;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.elements.ElementContext;
import com.transcendruins.assets.entities.EntityContext;
import com.transcendruins.assets.extra.Range;
import com.transcendruins.assets.extra.WeightedRoll;
import com.transcendruins.assets.layouts.LayoutAttributes.AssetGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.BlueprintGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.DistributionGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.GridGenerationSchema;
import com.transcendruins.assets.layouts.LayoutAttributes.LayoutGenerationSchema;
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

    public final Dimension getDimensions() {

        return generation.getDimensions();
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

        return switch (schema) {

        case AssetGenerationSchema assetSchema -> new AssetGenerationInstance(assetSchema);

        case LayoutGenerationSchema layoutSchema -> new LayoutGenerationInstance(layoutSchema);

        case DistributionGenerationSchema distributionSchema -> new DistributionGenerationInstance(distributionSchema);

        case GridGenerationSchema gridSchema -> new GridGenerationInstance(gridSchema);

        case BlueprintGenerationSchema blueprintSchema -> new BlueprintGenerationInstance(blueprintSchema);

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

        public abstract Dimension getDimensions();

        protected final AreaGrid createArea() {

            return new AreaGrid(getDimensions());
        }

        public abstract void generate(AreaGrid area);
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

                ElementContext context = new ElementContext(presets, world, null);
                yield context.instantiate();
            }

            case ENTITY -> {

                EntityContext context = new EntityContext(presets, world, null);
                yield context.instantiate();
            }

            case LAYOUT -> {

                LayoutContext context = new LayoutContext(presets, world, null);
                yield context.instantiate();
            }

            case null, default -> null;
            };
        }

        @Override
        public final Dimension getDimensions() {

            return switch (asset) {

            case PrimaryAssetInstance primary -> {

                Rectangle bounds = primary.getTileBounds();

                yield new Dimension(bounds.x + bounds.width, bounds.y + bounds.height);
            }

            case LayoutInstance layout -> layout.getDimensions();

            default -> new Dimension();
            };
        }

        @Override
        public final void generate(AreaGrid area) {

            switch (asset) {

            case PrimaryAssetInstance primary -> {

                area.apply(primary);
            }

            case LayoutInstance layout -> {

                area.apply(layout);
            }

            default -> {
            }
            }
        }
    }

    public final class LayoutGenerationInstance extends GenerationInstance {

        public LayoutGenerationInstance(LayoutGenerationSchema schema) {

            super(schema);
        }

        @Override
        public final Dimension getDimensions() {

            return new Dimension();
        }

        @Override
        public void generate(AreaGrid area) {
        }
    }

    public final class DistributionGenerationInstance extends GenerationInstance {

        public DistributionGenerationInstance(DistributionGenerationSchema schema) {

            super(schema);
        }

        @Override
        public final Dimension getDimensions() {

            return new Dimension();
        }

        @Override
        public void generate(AreaGrid area) {
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
        public final Dimension getDimensions() {

            return new Dimension();
        }

        @Override
        public void generate(AreaGrid area) {
        }
    }

    public final class BlueprintGenerationInstance extends GenerationInstance {

        public BlueprintGenerationInstance(BlueprintGenerationSchema schema) {

            super(schema);
        }

        @Override
        public final Dimension getDimensions() {

            return new Dimension();
        }

        @Override
        public void generate(AreaGrid area) {
        }
    }

    public final AreaGrid generate() {

        AreaGrid area = generation.createArea();
        generation.generate(area);

        return area;
    }

    @Override
    protected final void onUpdate(double time) {
    }
}
