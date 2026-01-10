package com.transcendruins.assets.layouts.placement;

import java.awt.Point;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;

public final record GenerationPlacement(WeightedRoll<GenerationPosition> center,
                WeightedRoll<GenerationDistribution> distribution, WeightedRoll<GenerationShapeSchema> shape) {

        public static final GenerationPlacement DEFAULT = new GenerationPlacement(
                        new WeightedRoll<>(GenerationPosition.DEFAULT),
                        new WeightedRoll<>(GenerationDistribution.UNIFORM),
                        new WeightedRoll<>(GenerationShapeSchema.PointGenerationShapeSchema.DEFAULT));

        private static GenerationPlacement createPlacement(TracedDictionary json) throws LoggedException {

                WeightedRoll<GenerationPosition> center = GenerationPosition.createPosition(json, "center");

                WeightedRoll<GenerationDistribution> distribution = GenerationDistribution.createDistribution(json,
                                "distribution");

                WeightedRoll<GenerationShapeSchema> shape = GenerationShapeSchema.createShape(json, "shape");

                return new GenerationPlacement(center, distribution, shape);
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
        public static final WeightedRoll<GenerationPlacement> createPlacement(TracedCollection json, Object key)
                        throws LoggedException {

                return json.getAsRoll(key, true, DEFAULT, entry -> createPlacement(entry.getValue()));
        }

        public final GenerationShapeInstance generateShape(PlacementArea area, int width, int length,
                        DeterministicRandom random) {

                GenerationPosition centerPosition = center().get(random.next());
                Point centerPoint = centerPosition.getPosition(area, width, length, random);

                GenerationDistribution distributionFunction = distribution().get(random.next());

                int areaWidth = area.getWidth();
                int areaLength = area.getLength();

                GenerationShapeSchema shapeSchema = shape().get(random.next());
                return GenerationShapeInstance.createShape(shapeSchema, random, distributionFunction, centerPoint.x,
                                centerPoint.y, areaWidth, areaLength, width, length);
        }
}
