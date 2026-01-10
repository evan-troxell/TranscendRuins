/* Copyright 2026 Evan Troxell
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
import java.util.Iterator;
import java.util.Map;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.animations.AnimationContext;
import com.transcendruins.assets.animations.AnimationInstance;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.assets.statecontrollers.StateControllerAttributes.AnimationStateSchema;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>StateControllerInstance</code>: A class representing a generated
 * animation controller instance.
 */
public final class StateControllerInstance extends AssetInstance {

    /**
     * <code>ImmutableMap&lt;String, AnimationStateSchema&gt;</code>: All animation
     * states of this <code>StateControllerInstance</code> instance.
     */
    private ImmutableMap<String, AnimationStateSchema> states;

    /**
     * <code>String</code>: The default animation state of this
     * <code>StateControllerInstance</code> instance.
     */
    private String defaultState;

    /**
     * <code>String</code>: The current state of this
     * <code>StateControllerInstance</code> instance.
     */
    private String state;

    /**
     * <code>double</code>: The time of creation of the current state of this
     * <code>StateControllerInstance</code> instance in seconds.
     */
    private double timeOfCreation;

    /**
     * <code>double</code>: The current timestamp of this
     * <code>StateControllerInstance</code> instance in seconds.
     */
    private double timestamp;

    /**
     * <code>ArrayList&lt;AnimationInstance&gt;</code>: The list animations
     * currently playing in this <code>StateControllerInstance</code> instance.
     */
    private final ArrayList<AnimationInstance> animations = new ArrayList<>();

    /**
     * <code>HashMap&lt;String, ImmutableList&lt;TRScript&gt;&gt;</code>: The map of
     * transitions of the current state of this <code>StateControllerInstance</code>
     * instance.
     */
    private final HashMap<String, ImmutableList<TRScript>> transitions = new HashMap<>();

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>StateControllerInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public StateControllerInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        StateControllerContext context = (StateControllerContext) assetContext;
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        StateControllerAttributes attributes = (StateControllerAttributes) attributeSet;

        states = calculateAttribute(attributes.getStates(), states);
        setProperty("states", new ImmutableList<>(states.keySet()));

        defaultState = calculateAttribute(attributes.getDefaultState(), defaultState);
        setProperty("defaultState", defaultState);
    }

    @Override
    protected final void onUpdate(double time) {

        if (state == null) {

            setState(defaultState, time);
        }
        state = calculateAttribute(evaluateTransitions(), newstate -> setState(newstate, time), state);
        setProperty("state", state);

        setProperty("transitions", new ImmutableList<>(transitions.keySet()));

        Iterator<AnimationInstance> animationIterator = animations.iterator();

        while (animationIterator.hasNext()) {

            AnimationInstance animation = animationIterator.next();
            if (timestamp > animation.getLength() && !animation.getHoldOnFinish()) {

                animationIterator.remove();
            }
        }

        HashMap<String, AnimationInstance> animationsMap = new HashMap<>();
        for (AnimationInstance animation : animations) {

            animationsMap.put(animation.getIdentifier().toString(), animation);
        }
        setProperty("animations", new ImmutableMap<>(animationsMap));

        timestamp = time - timeOfCreation;
        setProperty("timestamp", timestamp);

        for (AnimationInstance animation : animations) {

            animation.update(time);
        }
    }

    /**
     * Advances the current state to another one.
     * 
     * @param state <code>String</code>: The name of the new state.
     * @param time  <code>double</code>: The timestamp of the new state.
     * @return <code>String</code>: The name of the new state.
     */
    private String setState(String state, double time) {

        timeOfCreation = time;
        timestamp = time;

        animations.clear();

        StateControllerAttributes.AnimationStateSchema schema = states.get(state);
        for (AssetPresets animationPresets : schema.getStateAnimations()) {

            AnimationContext animationContext = new AnimationContext(animationPresets, getWorld(), this);
            animations.add((AnimationInstance) animationContext.instantiate());
        }

        transitions.clear();
        transitions.putAll(schema.getStateTransitions());

        return state;
    }

    /**
     * Evaluates the transitions of this <code>StateControllerInstance</code>
     * instance.
     * 
     * @return <code>String</code>: The first applicable state transition, or
     *         <code>null</code> if no state transitions are applicable.
     */
    private String evaluateTransitions() {

        for (Map.Entry<String, ImmutableList<TRScript>> transition : transitions.entrySet()) {

            String newState = transition.getKey();

            for (TRScript key : transition.getValue()) {

                if (key.evaluateBoolean(StateControllerInstance.this)) {

                    return newState;
                }
            }
        }

        return null;
    }

    /**
     * Evaluates the animations of this <code>StateControllerInstance</code>
     * instance, generating a set of bone actors.
     * 
     * @return <code>BoneActorSet</code>: The constructed map of bone actors.
     */
    public final BoneActorSet evaluatePose() {

        // If there is not a current state or there are no animations, do not apply any
        // animations.
        if (state == null || animations.isEmpty()) {

            return new BoneActorSet();
        }

        ArrayList<BoneActorSet> boneActors = new ArrayList<>();
        for (AnimationInstance animation : animations) {

            boneActors.add(animation.getKeyFrames(timestamp));
        }

        // Compile animations into a single set.
        return new BoneActorSet(boneActors);
    }
}
