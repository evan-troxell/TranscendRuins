package com.transcendruins.world.assetinstances.animationcontrollers;

import java.util.HashMap;
import java.util.HashSet;

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
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * @param schema <code>AnimationControllerSchema</code>: The schema used to generate this <code>AnimationControllerInstance</code> instance.
     * @param world <code>World</code>: The world copy to assign to this <code>AnimationControllerInstance</code> instance.
     */
    public AnimationControllerInstance(AnimationControllerSchema schema, World world) {

        super(schema, world);
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

            animations = new FinalizedMap<>();

            for (Identifier animationIdentifier : attributeAnimations) {

                animations.put(animationIdentifier, (AnimationSchema) getSchema(AssetType.ANIMATION, animationIdentifier));
            }

            animations.finalizeData();
        }
    }

    public final class AnimationStateInstance {

        private final double timeOfCreation = getWorld().getRuntimeSeconds();

        private final FinalizedList<AnimationInstance> stateAnimations = new FinalizedList<>();

        private final FinalizedMap<String, FinalizedList<TRScript>> stateTransitions = new FinalizedMap<>();

        private AnimationStateInstance(AnimationControllerSchemaAttributes.AnimationStateSchema schema) {

            for (Identifier animationIdentifier : schema.getStateAnimations()) {

                stateAnimations.add(new AnimationInstance(animations.get(animationIdentifier), getWorld()));
            }
        }
    }

    /**
     * Retrieves the bone actors of this <code>AnimationControllerInstance</code> instance.
     * @return <code>HashMap&lt;String, BoneActor&gt;</code>: The generated set of bone actors.
     */
    public HashMap<String, BoneActor> getBoneActors() {

        return null;
    }
}
