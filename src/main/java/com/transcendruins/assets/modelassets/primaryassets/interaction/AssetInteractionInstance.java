package com.transcendruins.assets.modelassets.primaryassets.interaction;

import java.awt.Point;
import java.util.List;
import java.util.Set;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.transcendruins.assets.AssetEvent;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.ExecuteInteractionAssetInteractionSchema;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.InventoryAssetInteractionSchema;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionSchema.PassagewayAssetInteractionSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.Player;
import com.transcendruins.world.PlayerSpawn;
import com.transcendruins.world.World;

public abstract class AssetInteractionInstance {

    private final Vector3f position;

    public final Vector3f getPosition(Quaternion rotation, Vector3f offset) {

        return rotation.mult(position).add(offset);
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
        duration = schema.getDuration();
        cooldown = schema.getCooldown();
        conditions = schema.getConditions();
        event = schema.getEvent();
    }

    public final void call(PrimaryAssetInstance self, double time, Player caller) {

        self.executeEvent(AssetEvent.ON_INTERACT);

        for (String eventKey : event) {

            self.executeEvent(eventKey);
        }

        onCall(self, time, caller);
    }

    protected abstract void onCall(PrimaryAssetInstance self, double time, Player caller);

    public static final AssetInteractionInstance createInteraction(AssetInteractionSchema schema) {

        return switch (schema) {

        case ExecuteInteractionAssetInteractionSchema executeSchema -> new ExecuteInteractionAssetInteractionInstance(
                executeSchema);

        case InventoryAssetInteractionSchema inventorySchema -> new InventoryAssetInteractionInstance(inventorySchema);

        case PassagewayAssetInteractionSchema passagewaySchema -> new PassagewayAssetInteractionInstance(
                passagewaySchema);

        default -> new AssetInteractionInstance(schema) {

            @Override
            public final void onCall(PrimaryAssetInstance self, double time, Player caller) {

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
        public final void onCall(PrimaryAssetInstance self, double time, Player caller) {

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
        public final void onCall(PrimaryAssetInstance self, double time, Player caller) {

            caller.displayInventory(self);
        }
    }

    public static final class PassagewayAssetInteractionInstance extends AssetInteractionInstance {

        public PassagewayAssetInteractionInstance(PassagewayAssetInteractionSchema schema) {

            super(schema);
        }

        @Override
        public final void onCall(PrimaryAssetInstance self, double time, Player caller) {

            World world = self.getWorld();
            EntityInstance playerEntity = caller.getEntity();
            DeterministicRandom random = caller.getRandom();

            String locationName = self.getProperty("passagewayLocation").evaluateString(playerEntity);
            boolean locationSpecified = locationName != null;

            String areaName = self.getProperty("passagewayArea").evaluateString(playerEntity);
            boolean areaSpecified = areaName != null;

            String targetTag = self.getProperty("passagewayTargetTag").evaluateString(playerEntity);
            boolean targetSpecified = targetTag != null;

            if (!locationSpecified && !areaSpecified && !targetSpecified) {

                return;
            }

            GlobalLocationInstance location = world
                    .getLocation(locationSpecified ? locationName : caller.getLocation());

            // Return if the specified location does not exist.
            if (location == null) {

                return;
            }

            AreaGrid area = areaSpecified ? location.getArea(areaName)
                    : (locationSpecified ? location.getArea(location.getPrimary()) : location.getArea(caller));

            // Return if the specified area does not exist.
            if (area == null) {

                return;
            }

            PlayerSpawn spawn = null;

            // Retrieve the spawn radius.
            double targetRange = self.getProperty("passagewayTargetRange").evaluateDouble(playerEntity);

            // Search for a specific element if a tag is provided.
            if (targetSpecified && targetRange >= 0) {

                // Get the shuffled matches.
                List<PrimaryAssetInstance> matches = area.getAssets(targetTag);
                random.shuffle(matches);

                PrimaryAssetInstance target = null;

                for (PrimaryAssetInstance match : matches) {

                    Point spawnPoint = area.getSpawnPoint(match, targetRange, playerEntity, random);
                    if (spawnPoint == null) {

                        continue;
                    }

                    spawn = new PlayerSpawn(areaName, spawnPoint.x, spawnPoint.y);
                    target = match;
                }

                double selfTargetRange = self.getProperty("passagewayTargetLinkRange").evaluateDouble(playerEntity);

                // Attempt to link the 2 elements together.
                boolean targetLink = self.getProperty("passagewayTargetLink").evaluateBoolean(playerEntity);
                if (target != null && targetLink && selfTargetRange >= 0
                        && self instanceof ElementInstance selfElement) {

                    String selfLocationName = caller.getLocation();
                    GlobalLocationInstance selfLocation = playerEntity.getLocation();

                    String selfAreaName = selfLocation.getAreaName(caller);
                    AreaGrid selfArea = selfLocation.getArea(selfAreaName);

                    // The elements can only be linked if both have tags.
                    Set<String> selfTag = selfArea.getTag(selfElement);
                    if (selfTag != null && !selfTag.isEmpty()) {

                        target.setPublicProperty("passagewayLocation", selfLocationName);
                        target.setPublicProperty("passagewayArea", selfAreaName);
                        target.setPublicProperty("passagewayTargetTag", selfTag.iterator().next());
                        target.setPublicProperty("passagewayTargetLink", false);
                        target.setPublicProperty("passagewayTargetRange", selfTargetRange);
                    }
                }
            }

            world.travel(caller, locationName, spawn);
        }
    }
}