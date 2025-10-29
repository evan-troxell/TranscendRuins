package com.transcendruins.assets.modelassets.primaryassets.interaction;

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.ExecuteInteractionAssetInteractionSchema;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.InventoryAssetInteractionSchema;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.PassagewayAssetInteractionSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.world.Player;

public abstract class AssetInteractionInstance {

    private final Vector position;

    public final Vector getPosition(Quaternion rotation, Vector offset) {

        return position.rotate(rotation).add(offset);
    }

    private final double cooldown;

    private long prevTime = -1;

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

    public AssetInteractionInstance(AssetInteractionSchema schema) {

        position = schema.getPosition();
        cooldown = schema.getCooldown();
        conditions = schema.getConditions();
        event = schema.getEvent();
    }

    public final boolean canCall(PrimaryAssetInstance self, long time) {

        // If it has been previously called and the interaction has not reset, do not
        // call again.
        if (prevTime > -1 && (cooldown == -1 || time - prevTime < 1000 * cooldown)) {

            return false;
        }

        return passes(self);
    }

    public final boolean call(PrimaryAssetInstance self, long time, Player caller) {

        if (!canCall(self, time)) {

            return false;
        }

        prevTime = time;

        for (String eventKey : event) {

            self.executeEvent(eventKey);
        }

        onCall(self, time, caller);
        return true;
    }

    protected abstract void onCall(PrimaryAssetInstance self, long time, Player caller);

    public static final AssetInteractionInstance createInteraction(AssetInteractionSchema schema) {

        return switch (schema) {

        case ExecuteInteractionAssetInteractionSchema executeSchema -> new ExecuteInteractionAssetInteractionInstance(
                executeSchema);

        case InventoryAssetInteractionSchema inventorySchema -> new InventoryAssetInteractionInstance(inventorySchema);

        case PassagewayAssetInteractionSchema passagewaySchema -> new PassagewayAssetInteractionInstance(
                passagewaySchema);

        default -> new AssetInteractionInstance(schema) {

            @Override
            public final void onCall(PrimaryAssetInstance self, long time, Player caller) {
            }
        };
        };
    }

    public static final class ExecuteInteractionAssetInteractionInstance extends AssetInteractionInstance {

        private final ImmutableList<AssetInteractionInstance> interaction;

        public ExecuteInteractionAssetInteractionInstance(ExecuteInteractionAssetInteractionSchema schema) {

            super(schema);

            interaction = new ImmutableList<>(
                    schema.getInteraction().stream().map(AssetInteractionInstance::createInteraction).toList());
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, long time, Player caller) {

            for (AssetInteractionInstance action : interaction) {

                action.call(self, time, caller);
            }
        }
    }

    public static final class InventoryAssetInteractionInstance extends AssetInteractionInstance {

        public InventoryAssetInteractionInstance(InventoryAssetInteractionSchema schema) {

            super(schema);
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, long time, Player caller) {

            caller.displayInventory(self);
        }
    }

    public static final class PassagewayAssetInteractionInstance extends AssetInteractionInstance {

        public PassagewayAssetInteractionInstance(PassagewayAssetInteractionSchema schema) {

            super(schema);
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, long time, Player caller) {
        }
    }
}