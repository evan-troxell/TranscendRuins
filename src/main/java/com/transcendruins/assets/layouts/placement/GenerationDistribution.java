package com.transcendruins.assets.layouts.placement;

import java.util.ArrayList;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;

public abstract class GenerationDistribution {

    public static final GenerationDistribution UNIFORM = new GenerationDistribution() {

        @Override
        public final double getChance(int x, int z, DeterministicRandom random) {

            return 1.0;
        }
    };

    public abstract double getChance(int x, int z, DeterministicRandom random);

    public static final GenerationDistribution createDistribution(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case "uniform" -> UNIFORM;

        case "normal" -> new NormalGenerationDistribution(json);

        case "peripheral" -> new PeripheralGenerationDistribution(json);

        case "ring" -> new RingGenerationDistribution(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final WeightedRoll<GenerationDistribution> createDistribution(TracedCollection collection, Object key)
            throws LoggedException {

        return collection.getAsRoll(key, true, GenerationDistribution.UNIFORM,
                entry -> createDistribution(entry.getValue()));
    }

    public static final class NormalGenerationDistribution extends GenerationDistribution {

        private final double spread;

        public NormalGenerationDistribution(TracedDictionary json) throws LoggedException {

            TracedEntry<Double> spreadEntry = json.getAsDouble("spread", true, 1.0, num -> num > 0);
            spread = spreadEntry.getValue();
        }

        @Override
        public double getChance(int x, int z, DeterministicRandom random) {

            return Math.exp(-(x * x + z * z) / (2 * spread * spread));
        }
    }

    public static final class PeripheralGenerationDistribution extends GenerationDistribution {

        private final double spread;

        public PeripheralGenerationDistribution(TracedDictionary json) throws LoggedException {

            TracedEntry<Double> spreadEntry = json.getAsDouble("spread", true, 1.0, num -> num > 0);
            spread = spreadEntry.getValue();
        }

        @Override
        public double getChance(int x, int z, DeterministicRandom random) {

            return Math.exp((x * x + z * z) / (2 * spread * spread));
        }
    }

    public static final class RingGenerationDistribution extends GenerationDistribution {

        private final double radius;

        private final double spread;

        public RingGenerationDistribution(TracedDictionary json) throws LoggedException {

            TracedEntry<Double> radiusEntry = json.getAsDouble("radius", false, null, num -> num > 0);
            radius = radiusEntry.getValue();

            TracedEntry<Double> spreadEntry = json.getAsDouble("spread", true, 1.0, num -> num > 0);
            spread = spreadEntry.getValue();
        }

        @Override
        public double getChance(int x, int z, DeterministicRandom random) {

            int r_sqr = x * x + z * z;

            return Math.exp(-(r_sqr + radius * radius - 2 * radius * Math.sqrt(r_sqr)) / (2 * spread * spread));
        }
    }

    public static final class SumGenerationDistribution extends GenerationDistribution {

        private final ImmutableList<GenerationDistribution> distributions;

        public SumGenerationDistribution(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedArray> distributionsEntry = json.getAsArray("distributions", false);
            TracedArray distributionsJson = distributionsEntry.getValue();

            ArrayList<GenerationDistribution> distributionsList = new ArrayList<>();
            for (int i : distributionsJson) {

                TracedEntry<TracedDictionary> distributionEntry = distributionsJson.getAsDict(i, false);
                TracedDictionary distributionJson = distributionEntry.getValue();

                distributionsList.add(createDistribution(distributionJson));
            }

            if (distributionsList.isEmpty()) {

                throw new CollectionSizeException(distributionsEntry, distributionsJson);
            }

            distributions = new ImmutableList<>(distributionsList);
        }

        @Override
        public double getChance(int x, int z, DeterministicRandom random) {

            return distributions.stream().mapToDouble(distribution -> distribution.getChance(x, z, random)).sum();
        }
    }
}
