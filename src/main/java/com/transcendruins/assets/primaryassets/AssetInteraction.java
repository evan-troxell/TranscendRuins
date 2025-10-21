package com.transcendruins.assets.primaryassets;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.world.Player;

public abstract class AssetInteraction {

    public static final String EXECUTE_INTERACTION = "executeInteraction";
    public static final String INVENTORY = "inventory";
    public static final String PASSAGEWAY = "passageway";

    public static final AssetInteraction NONE = new AssetInteraction() {

        @Override
        public final void onCall(PrimaryAssetInstance self, Player caller) {
        }
    };

    private final double cooldown;

    private long prevCall = -1;

    private final ImmutableList<TRScript> conditions;

    public final boolean passes(AssetInstance asset) {

        for (TRScript condition : conditions) {

            if (!condition.evaluateBoolean(asset)) {

                return false;
            }
        }

        return true;
    }

    private final ImmutableList<String> event;

    private AssetInteraction() {

        cooldown = 0.0;
        conditions = new ImmutableList<>();
        event = new ImmutableList<>();
    }

    public AssetInteraction(TracedDictionary json, AssetSchema schema) throws LoggedException {

        TracedEntry<Double> cooldownEntry = json.getAsDouble("cooldown", true, 0.0, num -> num >= 0.0 || num == -1.0);
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

                if (!schema.containsEvent(eventKey)) {

                    throw new ReferenceWithoutDefinitionException(eventEntry, "Event");
                }

                eventsList.add(eventKey);
            }
            return new ImmutableList<>(eventsList);
        }), json.stringCase(entry -> {

            String eventKey = entry.getValue();

            if (!schema.containsEvent(eventKey)) {

                throw new ReferenceWithoutDefinitionException(entry, "Event");
            }
            return new ImmutableList<>(eventKey);
        })));
    }

    public final boolean call(PrimaryAssetInstance self, Player caller) {

        long time = System.currentTimeMillis();

        // If it has been previously called and the interaction has not reset, do not
        // call again.
        if (prevCall > -1 && (cooldown == -1 || time - prevCall < 1000 * cooldown)) {

            return false;
        }

        if (!passes(self)) {

            return false;
        }

        prevCall = time;

        for (String eventKey : event) {

            self.executeEvent(eventKey);
        }

        onCall(self, caller);
        return true;
    }

    protected abstract void onCall(PrimaryAssetInstance self, Player caller);

    public static final AssetInteraction createInteraction(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        return switch (type) {

        case EXECUTE_INTERACTION -> new ExecuteInteractionAssetInteraction(json);

        case INVENTORY -> new InventoryAssetInteraction(json);

        case PASSAGEWAY -> new PassagewayAssetInteraction(json);

        case "none" -> AssetInteraction.NONE;

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final class ExecuteInteractionAssetInteraction extends AssetInteraction {

        private final ImmutableList<AssetInteraction> interaction;

        public ExecuteInteractionAssetInteraction(TracedDictionary json) throws LoggedException {

            interaction = json.get("interaction", List.of(
                    // Process a dictionary into a single interaction.
                    json.dictCase(entry -> {

                        TracedDictionary actionJson = entry.getValue();
                        return new ImmutableList<>(createInteraction(actionJson));
                    }),

                    // Process an array into a list of interactions.
                    json.arrayCase(entry -> {

                        ArrayList<AssetInteraction> actionsList = new ArrayList<>();

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

        @Override
        public final void onCall(PrimaryAssetInstance self, Player caller) {

            for (AssetInteraction action : interaction) {

                action.call(self, caller);
            }
        }
    }

    public static final class InventoryAssetInteraction extends AssetInteraction {

        public InventoryAssetInteraction(TracedDictionary json) {
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, Player caller) {

            caller.displayInventory(self);
        }
    }

    public static final class PassagewayAssetInteraction extends AssetInteraction {

        public PassagewayAssetInteraction(TracedDictionary json) {
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, Player caller) {
        }
    }
}