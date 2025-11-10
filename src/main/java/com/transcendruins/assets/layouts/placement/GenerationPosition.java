package com.transcendruins.assets.layouts.placement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.utilities.selection.WeightedRoll;

public abstract class GenerationPosition {

    public abstract Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random);

    public static final GenerationPosition DEFAULT = new GenerationPosition() {

        @Override
        public final Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random) {

            return new Point();
        }
    };

    public static final GenerationPosition createPosition(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case "absolute" -> new AbsoluteGenerationPosition(json);

        case "relative" -> new RelativeGenerationPosition(json);

        case "center" -> new CenterGenerationPosition(json);

        case "tag" -> new TagGenerationPosition(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final WeightedRoll<GenerationPosition> createPosition(TracedCollection collection, Object key)
            throws LoggedException {

        return collection.getAsRoll(key, true, DEFAULT, entry -> createPosition(entry.getValue()));
    }

    public static final class AbsoluteGenerationPosition extends GenerationPosition {

        private final int x;

        private final int z;

        public AbsoluteGenerationPosition(TracedDictionary json) throws LoggedException {

            TracedEntry<Integer> xEntry = json.getAsInteger("x", false, null);
            x = xEntry.getValue();

            TracedEntry<Integer> zEntry = json.getAsInteger("z", false, null);
            z = zEntry.getValue();
        }

        @Override
        public final Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random) {

            return new Point(x, z);
        }
    }

    public static final class RelativeGenerationPosition extends GenerationPosition {

        private final double xPercent;

        private final double zPercent;

        public RelativeGenerationPosition(TracedDictionary json) throws LoggedException {

            TracedEntry<Double> xPercentEntry = json.getAsDouble("xPercent", false, null,
                    num -> 0 <= num && num <= 100);
            xPercent = xPercentEntry.getValue();

            TracedEntry<Double> zPercentEntry = json.getAsDouble("zPercent", false, null,
                    num -> 0 <= num && num <= 100);
            zPercent = zPercentEntry.getValue();
        }

        @Override
        public final Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random) {

            return new Point((int) ((area.getWidth() - width) * xPercent / 100.0),
                    (int) ((area.getLength() - length) * zPercent / 100.0));
        }
    }

    public static final class CenterGenerationPosition extends GenerationPosition {

        public CenterGenerationPosition(TracedDictionary json) throws LoggedException {
        }

        @Override
        public final Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random) {

            return new Point((area.getWidth() - width) / 2, (area.getLength() - length) / 2);
        }
    }

    public static final class TagGenerationPosition extends GenerationPosition {

        private final String tag;

        public TagGenerationPosition(TracedDictionary json) throws LoggedException {

            TracedEntry<String> tagEntry = json.getAsString("tag", false, null);
            tag = tagEntry.getValue();
        }

        @Override
        public final Point getPosition(PlacementArea area, int width, int length, DeterministicRandom random) {

            ArrayList<Rectangle> matches = new ArrayList<>(area.getMatches(tag));
            if (matches.isEmpty()) {

                return new Point();
            }

            Rectangle bounds = random.next(matches);
            return new Point(bounds.x + (bounds.width - width) / 2, bounds.y + (bounds.height - length) / 2);
        }
    }
}
