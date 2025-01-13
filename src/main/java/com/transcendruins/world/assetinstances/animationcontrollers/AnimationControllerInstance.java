package com.transcendruins.world.assetinstances.animationcontrollers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchema;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchema;
import com.transcendruins.rendering.Model.BoneActor;
import com.transcendruins.utilities.finalize.FinalizedList;
import com.transcendruins.utilities.finalize.FinalizedMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.scripts.TRScript;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.AssetInstance;
import com.transcendruins.world.assetinstances.animations.AnimationInstance;

/**
 * <code>AnimationControllerInstance</code>: A class representing a generated animation controller instance.
 */
public final class AnimationControllerInstance extends AssetInstance {

    /**
     * <code>FinalizedMap&lt;Identifier, AnimationSchema&gt;</code>: All animations of this <code>AnimationControllerInstance</code> instance.
     */
    private FinalizedMap<Identifier, AnimationSchema> animations;

    /**
     * <code>FinalizedMap&lt;String, AnimationControllerSchemaAttributes.AnimationStateSchema&lt;</code>: All animation states of this <code>AnimationControllerInstance</code> instance.
     */
    private FinalizedMap<String, AnimationControllerSchemaAttributes.AnimationStateSchema> states;

    /**
     * <code>String</code>: The default animation state of this <code>AnimationControllerInstance</code> instance.
     */
    private String defaultState;

    /**
     * <code>AnimationStateInstance</code>: The current state of this <code>AnimationControllerInstance</code> instance.
     */
    private AnimationStateInstance state;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * @param schema <code>AnimationControllerSchema</code>: The schema used to generate this <code>AnimationControllerInstance</code> instance.
     * @param world <code>World</code>: The world copy to assign to this <code>AnimationControllerInstance</code> instance.
     */
    public AnimationControllerInstance(AnimationControllerSchema schema, World world) {

        super(schema, world);

        state = new AnimationStateInstance(states.get(defaultState));
    }

    /**
     * Applies a attribute set to this <code>AnimationControllerInstance</code> instance.
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        AnimationControllerSchemaAttributes attributes = (AnimationControllerSchemaAttributes) attributeSet;

        HashSet<Identifier> attributeAnimations = attributes.getAnimations();
        if (attributeAnimations != null) {

            for (Identifier animationIdentifier : attributeAnimations) {

                animations.put(animationIdentifier, (AnimationSchema) getSchema(AssetType.ANIMATION, animationIdentifier));
            }

        }

        states = attributes.getStates();

        defaultState = attributes.getDefaultState();
    }

    /**
     * <code>AnimationControllerInstance.AnimationStateInstance</code>: A subclass representing a specific state of this <code>AnimationControllerInstance</code> instance.
     */
    public final class AnimationStateInstance {

        /**
         * <code>double</code>: The time of creation of this <code>AnimationControllerInstance.AnimationStateInstance</code> instance in seconds.
         */
        private final double timeOfCreation = getWorld().getRuntimeSeconds();

        /**
         * <code>FinalizedList&lt;AnimationInstance&gt;</code>: The list of all animations of this <code>AnimationControllerInstance.AnimationStateInstance</code> instance.
         */
        private final FinalizedList<AnimationInstance> stateAnimations = new FinalizedList<>();

        /**
         * <code>FinalizedMap&lt;String, FinalizedList&lt;TRScript&gt;&gt;</code>: The map of all transitions of this <code>AnimationControllerInstance.AnimationStateInstance</code> instance.
         */
        private final FinalizedMap<String, FinalizedList<TRScript>> stateTransitions;

        /**
         * Creates a new instance of the <code>AnimationControllerInstance.AnimationStateInstance</code> class.
         * @param schema
         */
        private AnimationStateInstance(AnimationControllerSchemaAttributes.AnimationStateSchema schema) {

            for (Identifier animationIdentifier : schema.getStateAnimations()) {

                stateAnimations.add(new AnimationInstance(animations.get(animationIdentifier), getWorld()));
            }

            stateAnimations.finalizeData();

            stateTransitions = schema.getStateTransitions();
        }

        /**
         * Evaluates the transitions of this <code>AnimationControllerInstance.AnimationStateInstance</code> instance.
         * @return <code>String</code>: The first applicable state transition, or <code>null</code> if no state transitions are applicable.
         */
        private String evaluateTransitions() {

            for (Map.Entry<String, FinalizedList<TRScript>> transition : stateTransitions.entrySet()) {

                String state = transition.getKey();

                for (TRScript key : transition.getValue()) {

                    if (key.evaluate()) {

                        return state;
                    }
                }
            }

            return null;
        }

        /**
         * Evaluates the animations of this <code>AnimationControllerInstance.AnimationStateInstance</code> instance.
         * @return <code>HashMap&lt;String, BoneActor&gt;</code>: The constructed map of bone actors.
         */
        private HashMap<String, BoneActor> evaluateAnimations() {

            double timestamp = getWorld().getRuntimeSeconds() - timeOfCreation;

            HashMap<String, BoneActor> boneActors = new HashMap<>();

            for (AnimationInstance animation : stateAnimations) {

                boneActors.putAll(animation.getKeyFrames(timestamp));
            }

            return boneActors;
        }
    }

    /**
     * Evaluates the animations of this <code>AnimationControllerInstance</code> instance, generating a set of bone actors.
     * @return <code>HashMap&lt;String, BoneActor&gt;</code>: The constructed map of bone actors.
     */
    public HashMap<String, BoneActor> evaluateAnimations() {

        return state.evaluateAnimations();
    }

    /**
     * Evaluates the transitions of this <code>AnimationControllerInstance</code> instance, automatically transitioning to the next state if applicable.
     */
    public void evaluateTransitions() {

        String newState = state.evaluateTransitions();
        if (newState == null) {

            return;
        }

        state = new AnimationStateInstance(states.get(newState));
    }
}
