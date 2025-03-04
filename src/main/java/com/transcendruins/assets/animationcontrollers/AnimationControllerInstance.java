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
import java.util.Iterator;
import java.util.Map;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.animations.AnimationContext;
import com.transcendruins.assets.animations.AnimationInstance;
import com.transcendruins.assets.animations.AnimationPresets;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.extra.BoneActorSet;
import com.transcendruins.assets.scripts.TRScriptValue;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>AnimationControllerInstance</code>: A class representing a generated
 * animation controller instance.
 */
public final class AnimationControllerInstance extends AssetInstance {

    /**
     * <code>ImmutableMap&lt;String, AnimationControllerAttributes.AnimationStateSchema&gt;</code>:
     * All animation states of this <code>AnimationControllerInstance</code>
     * instance.
     */
    private ImmutableMap<String, AnimationControllerAttributes.AnimationStateSchema> states;

    /**
     * <code>String</code>: The default animation state of this
     * <code>AnimationControllerInstance</code> instance.
     */
    private String defaultState;

    /**
     * <code>String</code>: The current state of this
     * <code>AnimationControllerInstance</code> instance.
     */
    private String state;

    /**
     * <code>double</code>: The time of creation of this
     * <code>AnimationControllerInstance</code> instance in seconds.
     */
    private double timeOfCreation;

    /**
     * <code>double</code>: The current timestamp of this
     * <code>AnimationControllerInstance</code> instance in seconds.
     */
    private double timestamp;

    /**
     * <code>ArrayList&lt;AnimationInstance&gt;</code>: The list animations
     * currently playing in this <code>AnimationControllerInstance</code> instance.
     */
    private ArrayList<AnimationInstance> animations;

    /**
     * <code>ImmutableMap&lt;String, ImmutableList&lt;TRScriptValue&gt;&gt;</code>:
     * The map of transitions of the current state of this
     * <code>AnimationControllerInstance</code> instance.
     */
    private ImmutableMap<String, ImmutableList<TRScriptValue>> transitions;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * 
     * @param context <code>AnimationControllerPresets</code>: The context used to
     *                generate this <code>AnimationControllerInstance</code>
     *                instance.
     */
    public AnimationControllerInstance(AnimationControllerContext context) {

        super(context);
        setParent(context.getParent());

        setState(defaultState, context.getRuntimeSeconds());
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        AnimationControllerAttributes attributes = (AnimationControllerAttributes) attributeSet;

        states = calculateAttribute(attributes.getStates(), states);
        setProperty("states", new ImmutableList<>(states.keySet()));

        defaultState = calculateAttribute(attributes.getDefaultState(), defaultState);
        setProperty("defaultState", defaultState);
    }

    @Override
    protected void onUpdate(double time) {

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

    private String setState(String state, double time) {

        timeOfCreation = time;
        timestamp = time;

        AnimationControllerAttributes.AnimationStateSchema schema = states.get(state);
        for (AnimationPresets animationPresets : schema.getStateAnimations()) {

            AnimationContext animationContext = new AnimationContext(animationPresets, getWorld(),
                    AnimationControllerInstance.this);
            animations.add(new AnimationInstance(animationContext));
        }

        transitions = schema.getStateTransitions();
        return state;
    }

    /**
     * Evaluates the transitions of this <code>AnimationControllerInstance</code>
     * instance.
     * 
     * @return <code>String</code>: The first applicable state transition, or
     *         <code>null</code> if no state transitions are applicable.
     */
    private String evaluateTransitions() {

        for (Map.Entry<String, ImmutableList<TRScriptValue>> transition : transitions.entrySet()) {

            String newState = transition.getKey();

            for (TRScriptValue key : transition.getValue()) {

                if (key.evaluateBoolean(AnimationControllerInstance.this)) {

                    return newState;
                }
            }
        }

        return null;
    }

    /**
     * Evaluates the animations of this <code>AnimationControllerInstance</code>
     * instance, generating a set of bone actors.
     * 
     * @return <code>BoneActorSet</code>: The constructed map of
     *         bone actors.
     */
    public BoneActorSet evaluatePose() {

        ArrayList<BoneActorSet> boneActors = new ArrayList<>();

        for (AnimationInstance animation : animations) {

            boneActors.add(animation.getKeyFrames(timestamp));
        }

        return new BoneActorSet(boneActors);
    }
}
