package com.transcendruins.assets.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ComponentAction</code>: An abstract class representing the schema of a
 * UI component action.
 */
public abstract class ComponentAction {

    public static final String EXECUTE_ACTION = "executeAction";
    public static final String OPEN_MENU = "openMenu";
    public static final String CLOSE_MENU = "closeMenu";
    public static final String SWAP_SLOTS = "swapSlots";
    public static final String INTERACT = "interact";
    public static final String ATTACK = "attack";
    public static final String OPEN_INVENTORY = "openInventory";

    /**
     * <code>ImmutableList&lt;TRScript&gt;</code>: The conditions required to be met
     * to apply this <code>ComponentAction</code> instance.
     */
    private final ImmutableList<TRScript> conditions;

    /**
     * Retrieves the conditions required to be met to apply this
     * <code>ComponentAction</code> instance.
     * 
     * @return <code>ImmutableList&lt;TRScript&gt;</code>: The
     *         <code>conditions</code> field of this <code>ComponentAction</code>
     *         instance.
     */
    public final ImmutableList<TRScript> getConditions() {

        return conditions;
    }

    /**
     * Creates a new instance of the <code>ComponentAction</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The dictionary to parse.
     * @throws LoggedException Thrown if the dictionary could not be parsed.
     */
    public ComponentAction(TracedDictionary json) throws LoggedException {

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
    }

    @FunctionalInterface
    public interface OnCall {

        public void onCall(InterfaceInstance asset, long playerId, TRScript value);
    }

    private ComponentAction() {

        conditions = new ImmutableList<>();
    }

    public static final ComponentAction createComponentAction(OnCall action) {

        return new ComponentAction() {

            @Override
            protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

                action.onCall(asset, playerId, value);
            }

        };
    }

    /**
     * Determines whether or not this <code>ComponentAction</code> instance passes
     * for a given asset.
     * 
     * @param asset <code>AssetInstance</code>: The asset to evaluate the conditions
     *              against.
     * @return <code>boolean</code>: Whether or not the conditions pass for the
     *         given asset.
     */
    public final boolean passes(AssetInstance asset) {

        for (TRScript condition : conditions) {

            if (!condition.evaluateBoolean(asset)) {

                return false;
            }
        }

        return true;
    }

    protected abstract void onCall(InterfaceInstance asset, long playerId, TRScript value);

    public final void call(InterfaceInstance asset, long playerId, TRScript value) {

        if (passes(asset)) {

            onCall(asset, playerId, value);
        }
    }

    /**
     * Creates a component action schema from a dictionary.
     * 
     * @param json <code>TracedDictionary</code>: The dictionary to parse.
     * @return <code>ComponentAction</code>: The created action schema.
     * @throws LoggedException Thrown if the dictionary could not be parsed.
     */
    public static final ComponentAction createAction(TracedDictionary json) throws LoggedException {

        TracedEntry<String> typeEntry = json.getAsString("type", false, null);
        String type = typeEntry.getValue();

        // TODO: Add rest of action types
        return switch (type) {

        case OPEN_MENU -> new OpenMenuComponentAction(json);

        case CLOSE_MENU -> new CloseMenuComponentAction(json);

        // case SHOW_COMPONENT

        // case HIDE_COMPONENT

        case EXECUTE_ACTION -> new ExecuteActionComponentAction(json);

        // case SET_PROPERTY

        // case SET_GLOBAL_PROPERTY

        case SWAP_SLOTS -> new SwapSlotsComponentAction(json);

        case INTERACT -> new InteractComponentAction(json);

        case ATTACK -> new AttackComponentAction(json);

        case OPEN_INVENTORY -> new OpenInventoryComponentAction(json);

        default -> throw new UnexpectedValueException(typeEntry);
        };
    }

    public static final class ExecuteActionComponentAction extends ComponentAction {

        private final int count;

        private final ImmutableList<ComponentAction> action;

        public ExecuteActionComponentAction(TracedDictionary json) throws LoggedException {

            super(json);

            TracedEntry<Integer> selectEntry = json.getAsInteger("count", true, -1, num -> num > 0 || num == -1);
            count = selectEntry.getValue();

            action = json.get("action", List.of(

                    // Process a dictionary into a single action.
                    json.dictCase(entry -> {

                        TracedDictionary actionJson = entry.getValue();
                        return new ImmutableList<>(createAction(actionJson));
                    }),

                    // Process an array into a list of actions.
                    json.arrayCase(entry -> {

                        ArrayList<ComponentAction> actionsList = new ArrayList<>();

                        TracedArray actionsJson = entry.getValue();
                        for (int i : actionsJson) {

                            TracedEntry<TracedDictionary> actionEntry = actionsJson.getAsDict(i, false);
                            TracedDictionary actionJson = actionEntry.getValue();

                            actionsList.add(createAction(actionJson));
                        }

                        return new ImmutableList<>(actionsList);
                    }),

                    // Process no actions.
                    json.nullCase(_ -> new ImmutableList<>())));
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            int num = count;

            for (ComponentAction subAction : action) {

                subAction.call(asset, playerId, value);

                num--;
                if (num == 0) {

                    break;
                }
            }
        }
    }

    public static final class OpenMenuComponentAction extends ComponentAction {

        private final String menu;

        public OpenMenuComponentAction(TracedDictionary json) throws LoggedException {

            super(json);

            TracedEntry<String> menuEntry = json.getAsString("menu", false, null);
            menu = menuEntry.getValue();
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            asset.getWorld().openMenu(playerId, menu);
        }
    }

    public static final class CloseMenuComponentAction extends ComponentAction {

        public CloseMenuComponentAction(TracedDictionary json) throws LoggedException {

            super(json);
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            asset.getWorld().closeMenu(playerId);
        }
    }

    public static final class SwapSlotsComponentAction extends ComponentAction {

        private final Function<InventoryInstance, InventorySlotInstance> first;

        private final Function<InventoryInstance, InventorySlotInstance> second;

        public SwapSlotsComponentAction(TracedDictionary json) throws LoggedException {

            super(json);

            first = json.get("first", List.of(json.intCase(entry -> {

                int slot = entry.getValue();
                return (inventory) -> inventory.getSlot(slot);
            }), json.stringCase(entry -> {

                String slot = entry.getValue();
                return (inventory) -> inventory.getSlot(slot);
            })));

            second = json.get("second", List.of(json.intCase(entry -> {

                int slot = entry.getValue();
                return (inventory) -> inventory.getSlot(slot);
            }), json.stringCase(entry -> {

                String slot = entry.getValue();
                return (inventory) -> inventory.getSlot(slot);
            })));
        }

        @Override
        protected final void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            asset.getWorld().playerConsumer(playerId, player -> {

                InventoryInstance inventory = player.getEntity().getInventory();
                InventorySlotInstance firstSlot = first.apply(inventory);
                InventorySlotInstance secondSlot = second.apply(inventory);

                if (firstSlot == null || secondSlot == null) {

                    return;
                }

                firstSlot.putSlot(secondSlot);
            });
        }
    }

    public static final class InteractComponentAction extends ComponentAction {

        public InteractComponentAction(TracedDictionary json) throws LoggedException {

            super(json);
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            long time = System.currentTimeMillis();
            asset.getWorld().playerConsumer(playerId, player -> player.interact(time));
        }
    }

    public static final class AttackComponentAction extends ComponentAction {

        public AttackComponentAction(TracedDictionary json) throws LoggedException {

            super(json);
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            long time = System.currentTimeMillis();
            asset.getWorld().playerConsumer(playerId, player -> player.attack(time));
        }
    }

    public static final class OpenInventoryComponentAction extends ComponentAction {

        public OpenInventoryComponentAction(TracedDictionary json) throws LoggedException {

            super(json);
        }

        @Override
        protected void onCall(InterfaceInstance asset, long playerId, TRScript value) {

            asset.getWorld().playerConsumer(playerId, player -> player.displayInventory(player.getEntity()));
        }
    }
}
