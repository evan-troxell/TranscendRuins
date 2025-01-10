package com.transcendruins.packcompiling.assetschemas.animationcontrollers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.IdentifierFormatException;
import com.transcendruins.utilities.exceptions.propertyexceptions.InvalidKeyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.scripts.TRScript;

/**
* <code>AnimationControllerSchemaAttributes</code>: A class which represents the modules of an <code>AnimationControllerSchema</code> instance.
*/
public final class AnimationControllerSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>HashSet&lt;Identifier&gt;</code>: The set of all animations of this <code>AnimationControllerSchemaAttributes</code> instance.
     */
    private final HashSet<Identifier> animations;

    /**
     * <code>HashMap&lt;String, AnimationStateSchema&gt;</code>: The map of all animation states of this <code>AnimationControllerSchemaAttributes</code> instance.
     */
    private final HashMap<String, AnimationStateSchema> states;

    /**
     * <code>String</code>: The default state of this <code>AnimationControllerSchemaAttributes</code> instance.
     */
    private final String defaultState;

    /**
     * Compiles this <code>AnimationControllerSchemaAttributes</code> instance into a completed instance.
     * @param schema <code>AnimationSchema</code>: The schema which created this <code>AnimationControllerSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>AnimationControllerSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>AnimationControllerSchemaAttributes</code> instance is the base module set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>AnimationControllerSchemaAttributes</code> instance.
     */
    public AnimationControllerSchemaAttributes(AnimationControllerSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<TracedDictionary> statesEntry = schemaJson.getAsDictionary("states", !isBase);

        if (statesEntry.containsValue()) {

            animations = new HashSet<>();
            states = new HashMap<>();

            TracedDictionary statesJson = statesEntry.getValue();

            TracedEntry<String> defaultStateEntry = schemaJson.getAsString("defaultState", true, "default");
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
            animations = null;
            states = null;
        }
    }

    /**
     * Retrieves the animations of this <code>AnimationSchemaAttributes</code> instance.
     * @return <code>HashSet&lt;Identifier&gt;</code>: A copy of the <code>animations</code> field of this <code>AnimationSchemaAttributes</code> instance.
     */
    public HashSet<Identifier> getAnimations() {

        return new HashSet<>(animations);
    }

    /**
     * <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code>: A subclass representing a specific state of this <code>AnimationControllerSchemaAttributes</code> instance.
     */
    public final class AnimationStateSchema {

        /**
         * <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary entry of all transitions in this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         */
        private final TracedEntry<TracedDictionary> transitionsEntry;

        /**
         * <code>HashSet&lt;Identifier&gt;</code>: The animations of this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         */
        private final HashSet<Identifier> stateAnimations = new HashSet<>();

        /**
         * <code>HashMap&lt;String, HashSet&lt;TRScript&gt;&gt;</code>: The transitions of this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         */
        private final HashMap<String, HashSet<TRScript>> stateTransitions = new HashMap<>();

        /**
         * Creates a new instance of the <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> class.
         * @param json <code>TracedDictionary</code>: The JSON used to compile this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         * @throws LoggedException Thrown if an exception is raised while processing this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         */
        private AnimationStateSchema(TracedDictionary json) throws MissingPropertyException, PropertyTypeException, IdentifierFormatException {

            TracedEntry<TracedArray> animationsEntry = json.getAsArray("animations", true);

            if (animationsEntry.containsValue()) {

                TracedArray animationsJson = animationsEntry.getValue();
                for (int i : animationsJson.getIndices()) {

                    TracedEntry<Identifier> animationEntry = animationsJson.getAsIdentifier(i, false);
                    Identifier animation = animationEntry.getValue();

                    animations.add(animation);
                    stateAnimations.add(animation);
                    addElementDependency(AssetType.ANIMATION, animationEntry);
                }
            }

            transitionsEntry = json.getAsDictionary("transitions", true);

            if (transitionsEntry.containsValue()) {

                TracedDictionary transitionsJson = transitionsEntry.getValue();

                for (String stateName : transitionsJson.getKeys()) {

                    HashSet<TRScript> transitions = new HashSet<>();

                    TracedEntry<TracedArray> stateTransitionsEntry = transitionsJson.getAsArray(stateName, false);
                    TracedArray stateTransitionsJson = stateTransitionsEntry.getValue();

                    for (int i : stateTransitionsJson.getIndices()) {

                        TracedEntry<TracedDictionary> stateTransitionEntry = stateTransitionsJson.getAsDictionary(i, false);

                        transitions.add(new TRScript(stateTransitionEntry));
                    }

                    stateTransitions.put(stateName, transitions);
                }
            }
        }

        /**
         * Retrieves the animations of this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         * @return <code>HashSet&lt;Identifier&gt;</code>: A copy of the <code>stateAnimations</code> field of this <code>AnimationControllerSchemaAttributes.AnimationStateSchema</code> instance.
         */
        public HashSet<Identifier> getStateAnimations() {

            return new HashSet<>(stateAnimations);
        } 
    }
}
