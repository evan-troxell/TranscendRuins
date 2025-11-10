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

package com.transcendruins.assets.statecontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.transcendruins.assets.AssetType.ANIMATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>StateControllerAttributes</code>: A class which represents the
 * attributes of an <code>StateControllerSchema</code> instance.
 */
public final class StateControllerAttributes extends AssetAttributes {

    /**
     * <code>ImmutableMap&lt;String, AnimationStateSchema&gt;</code>: The map of all
     * animation states of this <code>StateControllerAttributes</code> instance.
     */
    private final ImmutableMap<String, AnimationStateSchema> states;

    /**
     * Retrieves the animation states of this <code>AnimationAttributes</code>
     * instance.
     * 
     * @return <code>ImmutableMap&lt;String, AnimationStateSchema&gt;</code>: The
     *         <code>states</code> field of this <code>AnimationAttributes</code>
     *         instance.
     */
    public ImmutableMap<String, AnimationStateSchema> getStates() {

        return states;
    }

    /**
     * <code>String</code>: The default state of this
     * <code>StateControllerAttributes</code> instance.
     */
    private final String defaultState;

    /**
     * Retrieves the default state of this <code>StateControllerAttributes</code>
     * instance.
     * 
     * @return <code>String</code>: The <code>defaultState</code> field of this
     *         <code>StateControllerAttributes</code> instance.
     */
    public String getDefaultState() {

        return defaultState;
    }

    /**
     * Compiles this <code>StateControllerAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>StateControllerAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>StateControllerAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>StateControllerAttributes</code> instance is the base
     *               attribute set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>StateControllerAttributes</code> instance.
     */
    public StateControllerAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        // The states should only be defined once.
        if (isBase) {

            HashMap<String, AssetPresets> animations = new HashMap<>();

            // Process the animations.
            TracedEntry<TracedDictionary> animationsEntry = json.getAsDict("animations", true);
            if (animationsEntry.containsValue()) {

                TracedDictionary animationsJson = animationsEntry.getValue();
                for (String animationString : animationsJson) {

                    TracedEntry<AssetPresets> animationEntry = animationsJson.getAsPresets(animationString, false,
                            ANIMATION);
                    AssetPresets animation = animationEntry.getValue();
                    addAssetDependency(animation);
                    animations.put(animationString, animation);
                }
            }

            HashMap<String, AnimationStateSchema> statesMap = new HashMap<>();

            // States are required.
            TracedEntry<TracedDictionary> statesEntry = json.getAsDict("states", false);
            TracedDictionary statesJson = statesEntry.getValue();

            TracedEntry<String> defaultStateEntry = json.getAsString("defaultState", true, "default");
            defaultState = defaultStateEntry.getValue();

            // Verify that the default state exists.
            statesJson.getAsDict(defaultState, false);

            // Process the available states.
            for (String stateName : statesJson) {

                TracedEntry<TracedDictionary> stateEntry = statesJson.getAsDict(stateName, false);
                TracedDictionary stateJson = stateEntry.getValue();

                AnimationStateSchema state = new AnimationStateSchema(stateJson, animations);
                statesMap.put(stateName, state);
            }

            states = new ImmutableMap<>(statesMap);

            // Validate that no states are missing.
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
     * <code>AnimationStateSchema</code>: A class representing a specific state of
     * this <code>StateControllerAttributes</code> instance.
     */
    public final class AnimationStateSchema {

        /**
         * <code>ImmutableList&lt;AssetPresets&gt;</code>: The animations of this
         * <code>AnimationStateSchema</code> instance.
         */
        private final ImmutableList<AssetPresets> stateAnimations;

        /**
         * Retrieves the animations of this <code>AnimationStateSchema</code> instance.
         * 
         * @return <code>ImmutableList&lt;AssetPresets&gt;</code>: The
         *         <code>stateAnimations</code> field of this
         *         <code>AnimationStateSchema</code> instance.
         */
        public final ImmutableList<AssetPresets> getStateAnimations() {

            return stateAnimations;
        }

        /**
         * <code>ImmutableMap&lt;String, ImmutableList&lt;TRScript&gt;&gt;</code>: The
         * transitions of this <code>AnimationStateSchema</code> instance.
         */
        private final ImmutableMap<String, ImmutableList<TRScript>> stateTransitions;

        /**
         * Retrieves the state transitions of this <code>AnimationStateSchema</code>
         * instance.
         * 
         * @return <code>ImmutableMap&lt;String, ImmutableList&lt;TRScript&gt;&gt;</code>:
         *         The <code>stateTransitions</code> field of this
         *         <code>AnimationStateSchema</code> instance.
         */
        public final ImmutableMap<String, ImmutableList<TRScript>> getStateTransitions() {

            return stateTransitions;
        }

        /**
         * Creates a new instance of the <code>AnimationStateSchema</code> class.
         * 
         * @param json       <code>TracedDictionary</code>: The JSON used to compile
         *                   this <code>AnimationStateSchema</code> instance.
         * @param animations <code>Map&lt;String, AssetPreset&gt;</code>: The predefined
         *                   animations of the animation controller.
         * @throws LoggedException Thrown if an exception is raised while processing
         *                         this <code>AnimationStateSchema</code> instance.
         */
        private AnimationStateSchema(TracedDictionary json, Map<String, AssetPresets> animations)
                throws LoggedException {

            TracedEntry<TracedArray> animationsEntry = json.getAsArray("animations", true);

            if (animationsEntry.containsValue()) {

                TracedArray animationsJson = animationsEntry.getValue();

                ArrayList<AssetPresets> stateAnimationsList = new ArrayList<>();
                for (int i : animationsJson) {

                    stateAnimationsList.add(animationsJson.get(i, List.of(animationsJson.stringCase(entry -> {

                        String animationString = entry.getValue();
                        if (animations.containsKey(animationString)) {

                            return animations.get(animationString);
                        }

                        TracedEntry<AssetPresets> animationEntry = animationsJson.getAsPresets(i, false, ANIMATION);
                        AssetPresets animation = animationEntry.getValue();
                        addAssetDependency(animation);
                        return animation;
                    }), animationsJson.presetsCase(entry -> {

                        AssetPresets animation = entry.getValue();
                        addAssetDependency(animation);
                        return animation;
                    }, ANIMATION))));
                }

                stateAnimations = new ImmutableList<>(stateAnimationsList);
            } else {

                stateAnimations = null;
            }

            // TODO Implement 'sounds'
            // TODO implement 'particles'

            TracedEntry<TracedDictionary> transitionsEntry = json.getAsDict("transitions", true);

            if (transitionsEntry.containsValue()) {

                TracedDictionary transitionsJson = transitionsEntry.getValue();

                HashMap<String, ImmutableList<TRScript>> stateTransitionsMap = new HashMap<>();

                for (String stateName : transitionsJson) {

                    ArrayList<TRScript> transitionsMap = new ArrayList<>();

                    TracedEntry<TracedArray> stateTransitionsEntry = transitionsJson.getAsArray(stateName, false);
                    TracedArray stateTransitionsJson = stateTransitionsEntry.getValue();

                    for (int i : stateTransitionsJson) {

                        transitionsMap.add(new TRScript(stateTransitionsJson, i));
                    }

                    stateTransitionsMap.put(stateName, new ImmutableList<>(transitionsMap));
                }

                stateTransitions = new ImmutableMap<>(stateTransitionsMap);
            } else {

                stateTransitions = null;
            }
        }
    }
}
