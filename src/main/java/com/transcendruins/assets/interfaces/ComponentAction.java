package com.transcendruins.assets.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.assets.AssetInstance;
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

    public static final String OPEN_MENU = "openMenu";
    public static final String CLOSE_MENU = "closeMenu";

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

    public abstract void call(AssetInstance instance);

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

        // case OPEN_MENU -> new OpenMenuComponentAction(json);

        case CLOSE_MENU -> null;// new CloseMenuComponentAction(json);

        // case SHOW_COMPONENT

        // case HIDE_COMPONENT

        // case COMPONENT_ACTION

        // case SET_PROPERTY

        // case SET_GLOBAL_PROPERTY

        case "PLACEHOLDER" -> null;

        case null, default -> throw new UnexpectedValueException(typeEntry);
        };
    }
}
