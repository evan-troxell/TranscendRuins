package com.transcendruins.packcompiling.assetschemas.animationcontrollers;

import java.util.Map;

import org.json.simple.JSONObject;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.finalize.FinalizedMap;
import com.transcendruins.utilities.finalize.FinalizedSet;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.scripts.TRScript;
import com.transcendruins.world.assetinstances.animations.AnimationPresets;

/**
 * <code>AnimationControllerSchemaAttributes</code>: A class which represents
 * the modules of an <code>AnimationControllerSchema</code> instance.
 */
public final class AnimationControllerSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>FinalizedMap&lt;String, AnimationStateSchema&gt;</code>: The map of all
     * animation states of this <code>AnimationControllerSchemaAttributes</code>
     * instance.
     */
    private final FinalizedMap<String, AnimationStateSchema> states;

    /**
     * Retrieves the animation states of this <code>AnimationSchemaAttributes</code>
     * instance.
     * 
     * @return <code>FinalizedMap&lt;String, AnimationStateSchema&gt;</code>: The
     *         <code>states</code> field of this
     *         <code>AnimationSchemaAttributes</code> instance.
     */
    public FinalizedMap<String, AnimationStateSchema> getStates() {

        return states;
    }

    /**
     * <code>String</code>: The default state of this
     * <code>AnimationControllerSchemaAttributes</code> instance.
     */
    private final String defaultState;

    /**
     * Retrieves the default state of this
     * <code>AnimationControllerSchemaAttributes</code> instance.
     * 
     * @return <code<String</code>: The <code>defaultState</code> field of this
     *         <code>AnimationControllerSchemaAttributes</code> instance.
     */
    public String getDefaultState() {

        return defaultState;
    }

    /**
     * Compiles this <code>AnimationControllerSchemaAttributes</code> instance into
     * a completed instance.
     * 
     * @param schema <code>AnimationSchema</code>: The schema which created this
     *               <code>AnimationControllerSchemaAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this
     *               <code>AnimationControllerSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>AnimationControllerSchemaAttributes</code> instance
     *               is the base module set of an <code>AnimationSchema</code>
     *               instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>AnimationControllerSchemaAttributes</code>
     *                         instance.
     */
    public AnimationControllerSchemaAttributes(AnimationControllerSchema schema, TracedDictionary json,
            boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<TracedDictionary> statesEntry = json.getAsDictionary("states", !isBase);

        if (statesEntry.containsValue()) {

            states = new FinalizedMap<>();

            TracedDictionary statesJson = statesEntry.getValue();

            TracedEntry<String> defaultStateEntry = json.getAsString("defaultState", true, "default");
            defaultState = defaultStateEntry.getValue();

            statesJson.getAsDictionary(defaultState, false);

            for (String stateName : statesJson.getKeys()) {

                TracedEntry<TracedDictionary> stateEntry = statesJson.getAsDictionary(stateName, false);
                TracedDictionary stateJson = stateEntry.getValue();

                AnimationStateSchema state = new AnimationStateSchema(stateJson);
                states.put(stateName, state);
            }

            for (Map.Entry<String, AnimationStateSchema> stateEntry : states.entrySet()) {

                String stateName = stateEntry.getKey();
                AnimationStateSchema state = stateEntry.getValue();

                if (state.stateTransitions.containsKey(stateName)) {

                    throw new InvalidKeyException(state.transitionsEntry, stateName);
                }

                for (String transitionStateName : state.stateTransitions.keySet()) {

                    statesJson.getAsDictionary(transitionStateName, false);
                }
            }
        } else {

            defaultState = null;
            states = null;
        }

        finalizeData();
    }

    /**
     * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>: A
     * subclass representing a specific state of this
     * <code>AnimationControllerSchemaAttributes</code> instance.
     */
    public final class AnimationStateSchema {

        /**
         * <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary entry of all
         * transitions in this
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final TracedEntry<TracedDictionary> transitionsEntry;

        /**
         * <code>FinalizedSet&lt;AnimationPresets&gt;</code>: The animations
         * of this
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final FinalizedSet<AnimationPresets> stateAnimations = new FinalizedSet<>();

        /**
         * <code>FinalizedMap&lt;String, FinalizedList&lt;TRScript&gt;&gt;</code>: The
         * transitions of this
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final FinalizedMap<String, FinalizedList<TRScript>> stateTransitions = new FinalizedMap<>();

        /**
         * Creates a new instance of the
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The JSON used to compile this
         *             <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         *             instance.
         * @throws LoggedException Thrown if an exception is raised while processing
         *                         this
         *                         <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         *                         instance.
         */
        private AnimationStateSchema(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedArray> animationsEntry = json.getAsArray("animations", true);

            if (animationsEntry.containsValue()) {

                TracedArray animationsJson = animationsEntry.getValue();
                for (int i : animationsJson.getIndices()) {

                    Object animationValue = animationsJson.get(i, false, null, String.class, JSONObject.class)
                            .getValue();

                    AnimationPresets animationPresets;

                    if (animationValue instanceof TracedDictionary animationJson) {

                        animationPresets = new AnimationPresets(animationJson);
                    } else {

                        TracedEntry<Identifier> animationEntry = animationsJson.getAsIdentifier(i, false);
                        animationPresets = new AnimationPresets(animationEntry);
                    }

                    stateAnimations.add(animationPresets);
                    addElementDependency(animationPresets);
                }
            }

            transitionsEntry = json.getAsDictionary("transitions", true);

            if (transitionsEntry.containsValue()) {

                TracedDictionary transitionsJson = transitionsEntry.getValue();

                for (String stateName : transitionsJson.getKeys()) {

                    FinalizedList<TRScript> transitions = new FinalizedList<>();

                    TracedEntry<TracedArray> stateTransitionsEntry = transitionsJson.getAsArray(stateName, false);
                    TracedArray stateTransitionsJson = stateTransitionsEntry.getValue();

                    for (int i : stateTransitionsJson.getIndices()) {

                        TracedEntry<TracedDictionary> stateTransitionEntry = stateTransitionsJson.getAsDictionary(i,
                                false);

                        transitions.add(new TRScript(stateTransitionEntry));
                    }

                    stateTransitions.put(stateName, transitions);
                }
            }
        }

        /**
         * Retrieves the animations of this
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         * instance.
         * 
         * @return <code>FinalizedSet&lt;AnimationPresets&gt;</code>: The
         *         <code>stateAnimations</code> field of this
         *         <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         *         instance.
         */
        public FinalizedSet<AnimationPresets> getStateAnimations() {

            return stateAnimations;
        }

        /**
         * Retrieves the state transitions of this
         * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         * instance.
         * 
         * @return <code>FinalizedMap&lt;String, FinalizedList&lt;TRScript&gt;&gt;</code>:
         *         The <code>stateTransitions</code> field of this
         *         <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>
         *         instance.
         */
        public FinalizedMap<String, FinalizedList<TRScript>> getStateTransitions() {

            return stateTransitions;
        }
    }

    @Override
    protected void finalizeData() {

        if (states != null) {

            states.finalizeData();

            for (AnimationStateSchema state : states.values()) {

                state.stateAnimations.finalizeData();
                state.stateTransitions.finalizeData();
            }
        }
    }
}
