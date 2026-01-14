package com.transcendruins.assets.modelassets.primaryassets.interaction;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public abstract class AssetInteractionSchema {

    public static final String EXECUTE_INTERACTION = "executeInteraction";
    public static final String INVENTORY = "inventory";
    public static final String PASSAGEWAY = "passageway";

    public static final AssetInteractionSchema NONE = new AssetInteractionSchema() {
    };

    private final Vector position;

    public final Vector getPosition() {

        return position;
    }

    private final double duration;

    public final double getDuration() {

        return duration;
    }

    private final double cooldown;

    public final double getCooldown() {

        return cooldown;
    }

    private final ImmutableList<TRScript> conditions;

    public final ImmutableList<TRScript> getConditions() {

        return conditions;
    }

    private final ImmutableList<String> event;

    public final ImmutableList<String> getEvent() {

        return event;
    }

    private AssetInteractionSchema() {

        position = Vector.IDENTITY_VECTOR;
        duration = 0.0;
        cooldown = 0.0;
        conditions = new ImmutableList<>();
        event = new ImmutableList<>();
    }

    public AssetInteractionSchema(TracedDictionary json) throws LoggedException {

        TracedEntry<Vector> positionEntry = json.getAsVector("position", true, 3);
        if (positionEntry.containsValue()) {

            position = positionEntry.getValue();
        } else {

            position = Vector.IDENTITY_VECTOR;
        }

        TracedEntry<Double> durationEntry = json.getAsDouble("duration", true, 0.0, num -> 0.0 <= num);
        duration = durationEntry.getValue();

        TracedEntry<Double> cooldownEntry = json.getAsDouble("cooldown", true, 0.0, num -> 0.0 <= num);
        cooldown = cooldownEntry.getValue();

        conditions = json.get("conditions", List.of(json.arrayCase(entry -> {

            ArrayList<TRScript> conditionsList = new ArrayList<>();

            TracedArray conditionsJson = entry.getValue();
            for (int i : conditionsJson) {

                TracedEntry<TRScript> conditionEntry = conditionsJson.getAsScript(i, false);
                TRScript condition = conditionEntry.getValue();
                conditionsList.add(condition);
            }

            return new ImmutableList<>(conditionsList);
        }), json.nullCase(_ -> new ImmutableList<>()), json.scriptCase(entry -> {

            TRScript condition = entry.getValue();
            return new ImmutableList<>(condition);
        })));

        event = json.get("event", List.of(json.arrayCase(entry -> {

            ArrayList<String> eventsList = new ArrayList<>();
            TracedArray eventsJson = entry.getValue();
            for (int i : eventsJson) {

                TracedEntry<String> eventEntry = eventsJson.getAsString(i, false, null);
                String eventKey = eventEntry.getValue();
                eventsList.add(eventKey);
            }
            return new ImmutableList<>(eventsList);
        }), json.stringCase(entry -> {

            String eventKey = entry.getValue();
            return new ImmutableList<>(eventKey);
        }), json.nullCase(_ -> new ImmutableList<>())));
    }

    public static final AssetInteractionSchema createInteraction(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case EXECUTE_INTERACTION -> new ExecuteInteractionAssetInteractionSchema(json);

        case INVENTORY -> new InventoryAssetInteractionSchema(json);

        case PASSAGEWAY -> new PassagewayAssetInteractionSchema(json);

        case "none" -> NONE;

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final class ExecuteInteractionAssetInteractionSchema extends AssetInteractionSchema {

        private final ImmutableList<AssetInteractionSchema> interaction;

        public final ImmutableList<AssetInteractionSchema> getInteraction() {

            return interaction;
        }

        public ExecuteInteractionAssetInteractionSchema(TracedDictionary json) throws LoggedException {

            super(json);

            interaction = json.get("interaction", List.of(
                    // Process a dictionary into a single interaction.
                    json.dictCase(entry -> {

                        TracedDictionary actionJson = entry.getValue();
                        return new ImmutableList<>(createInteraction(actionJson));
                    }),

                    // Process an array into a list of interactions.
                    json.arrayCase(entry -> {

                        ArrayList<AssetInteractionSchema> actionsList = new ArrayList<>();

                        TracedArray actionsJson = entry.getValue();
                        for (int i : actionsJson) {

                            TracedEntry<TracedDictionary> actionEntry = actionsJson.getAsDict(i, false);
                            TracedDictionary actionJson = actionEntry.getValue();

                            actionsList.add(createInteraction(actionJson));
                        }

                        return new ImmutableList<>(actionsList);
                    }),

                    // Process no interactions.
                    json.nullCase(_ -> new ImmutableList<>())));
        }
    }

    public static final class InventoryAssetInteractionSchema extends AssetInteractionSchema {

        public InventoryAssetInteractionSchema(TracedDictionary json) throws LoggedException {

            super(json);
        }
    }

    public static final class PassagewayAssetInteractionSchema extends AssetInteractionSchema {

        public PassagewayAssetInteractionSchema(TracedDictionary json) throws LoggedException {

            super(json);
        }
    }
}