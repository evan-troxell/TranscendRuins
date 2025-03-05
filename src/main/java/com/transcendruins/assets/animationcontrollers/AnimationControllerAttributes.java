/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.animationcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.transcendruins.assets.AssetType.ANIMATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.scripts.TRScriptValue;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>AnimationControllerAttributes</code>: A class which represents
 * the attributes of an <code>AnimationControllerSchema</code> instance.
 */
public final class AnimationControllerAttributes extends AssetAttributes {

    /**
     * <code>ImmutableMap&lt;String, AnimationStateSchema&gt;</code>: The map of all
     * animation states of this <code>AnimationControllerAttributes</code>
     * instance.
     */
    private final ImmutableMap<String, AnimationStateSchema> states;

    /**
     * Retrieves the animation states of this <code>AnimationAttributes</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, AnimationStateSchema&gt;</code>: The
     *         <code>states</code> field of this
     *         <code>AnimationAttributes</code> instance.
     */
    public ImmutableMap<String, AnimationStateSchema> getStates() {

        return states;
    }

    /**
     * <code>String</code>: The default state of this
     * <code>AnimationControllerAttributes</code> instance.
     */
    private final String defaultState;

    /**
     * Retrieves the default state of this
     * <code>AnimationControllerAttributes</code> instance.
     * 
     * @return <code>String</code>: The <code>defaultState</code> field of this
     *         <code>AnimationControllerAttributes</code> instance.
     */
    public String getDefaultState() {

        return defaultState;
    }

    /**
     * Compiles this <code>AnimationControllerAttributes</code> instance into
     * a completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>AnimationControllerAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this
     *               <code>AnimationControllerAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>AnimationControllerAttributes</code> instance
     *               is the base attribute set of an <code>AnimationSchema</code>
     *               instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>AnimationControllerAttributes</code>
     *                         instance.
     */
    public AnimationControllerAttributes(AssetSchema schema, TracedDictionary json,
            boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<TracedDictionary> statesEntry = json.getAsDict("states", !isBase);

        if (statesEntry.containsValue()) {

            if (!isBase) {

                throw new KeyNameException("states", statesEntry);
            }

            HashMap<String, AnimationStateSchema> statesMap = new HashMap<>();

            TracedDictionary statesJson = statesEntry.getValue();

            TracedEntry<String> defaultStateEntry = json.getAsString("defaultState", true, "default");
            defaultState = defaultStateEntry.getValue();

            statesJson.getAsDict(defaultState, false);

            for (String stateName : statesJson.getKeys()) {

                TracedEntry<TracedDictionary> stateEntry = statesJson.getAsDict(stateName, false);
                TracedDictionary stateJson = stateEntry.getValue();

                AnimationStateSchema state = new AnimationStateSchema(stateJson);
                statesMap.put(stateName, state);
            }

            states = new ImmutableMap<>(statesMap);

            for (Map.Entry<String, AnimationStateSchema> stateEntry : states.entrySet()) {

                AnimationStateSchema state = stateEntry.getValue();

                for (String transitionStateName : state.stateTransitions.keySet()) {

                    statesJson.getAsDict(transitionStateName, false);
                }
            }
        } else {

            defaultState = null;
            states = null;
        }
    }

    /**
     * <code>AnimationControllerAttributes.AnimationStateSchema</code>: A
     * subclass representing a specific state of this
     * <code>AnimationControllerAttributes</code> instance.
     */
    public final class AnimationStateSchema {

        /**
         * <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary entry of all
         * transitions in this
         * <code>AnimationControllerAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final TracedEntry<TracedDictionary> transitionsEntry;

        /**
         * <code>ImmutableList&lt;AssetPresets&gt;</code>: The animations
         * of this
         * <code>AnimationControllerAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final ImmutableList<AssetPresets> stateAnimations;

        /**
         * <code>ImmutableMap&lt;String, ImmutableList&lt;TRScript&gt;&gt;</code>: The
         * transitions of this
         * <code>AnimationControllerAttributes.AnimationStateSchema</code>
         * instance.
         */
        private final ImmutableMap<String, ImmutableList<TRScriptValue>> stateTransitions;

        /**
         * Creates a new instance of the
         * <code>AnimationControllerAttributes.AnimationStateSchema</code> class.
         * 
         * @param json <code>TracedDictionary</code>: The JSON used to compile this
         *             <code>AnimationControllerAttributes.AnimationStateSchema</code>
         *             instance.
         * @throws LoggedException Thrown if an exception is raised while processing
         *                         this
         *                         <code>AnimationControllerAttributes.AnimationStateSchema</code>
         *                         instance.
         */
        private AnimationStateSchema(TracedDictionary json) throws LoggedException {

            TracedEntry<TracedArray> animationsEntry = json.getAsArray("animations", true);

            if (animationsEntry.containsValue()) {

                TracedArray animationsJson = animationsEntry.getValue();

                ArrayList<AssetPresets> stateAnimationsList = new ArrayList<>();
                for (int i : animationsJson.getIndices()) {

                    AssetPresets animationPresets = ANIMATION.createPresets(animationsJson, i, false);

                    stateAnimationsList.add(animationPresets);
                    addAssetDependency(animationPresets);
                }

                stateAnimations = new ImmutableList<>(stateAnimationsList);
            } else {

                stateAnimations = null;
            }

            transitionsEntry = json.getAsDict("transitions", true);

            if (transitionsEntry.containsValue()) {

                TracedDictionary transitionsJson = transitionsEntry.getValue();

                HashMap<String, ImmutableList<TRScriptValue>> stateTransitionsMap = new HashMap<>();

                for (String stateName : transitionsJson.getKeys()) {

                    ArrayList<TRScriptValue> transitionsMap = new ArrayList<>();

                    TracedEntry<TracedArray> stateTransitionsEntry = transitionsJson.getAsArray(stateName, false);
                    TracedArray stateTransitionsJson = stateTransitionsEntry.getValue();

                    for (int i : stateTransitionsJson.getIndices()) {

                        transitionsMap.add(new TRScriptValue(stateTransitionsJson, i));
                    }

                    stateTransitionsMap.put(stateName, new ImmutableList<>(transitionsMap));
                }

                stateTransitions = new ImmutableMap<>(stateTransitionsMap);
            } else {

                stateTransitions = null;
            }
        }

        /**
         * Retrieves the animations of this
         * <code>AnimationControllerAttributes.AnimationStateSchema</code>
         * instance.
         * 
         * @return <code>ImmutableList&lt;AssetPresets&gt;</code>: The
         *         <code>stateAnimations</code> field of this
         *         <code>AnimationControllerAttributes.AnimationStateSchema</code>
         *         instance.
         */
        public ImmutableList<AssetPresets> getStateAnimations() {

            return stateAnimations;
        }

        /**
         * Retrieves the state transitions of this
         * <code>AnimationControllerAttributes.AnimationStateSchema</code>
         * instance.
         * 
         * @return <code>ImmutableMap&lt;String, ImmutableList&lt;TRScript&gt;&gt;</code>:
         *         The <code>stateTransitions</code> field of this
         *         <code>AnimationControllerAttributes.AnimationStateSchema</code>
         *         instance.
         */
        public ImmutableMap<String, ImmutableList<TRScriptValue>> getStateTransitions() {

            return stateTransitions;
        }
    }
}
