package com.transcendruins.world.assetinstances.animationcontrollers;

import java.util.HashMap;
import java.util.HashSet;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchema;
import com.transcendruins.packcompiling.assetschemas.animationcontrollers.AnimationControllerSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.animations.AnimationSchema;
import com.transcendruins.rendering.Model.BoneActor;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.assetinstances.AssetInstance;

/**
 * <code>AnimationControllerInstance</code>: A class representing a generated animation controller instance.
 */
public final class AnimationControllerInstance extends AssetInstance {

    /**
     * <code>HashMap&lt;Identifier, AnimationSchema&gt;</code>: All animations of this <code>AnimationControllerInstance</code> instance.
     */
    private HashMap<Identifier, AnimationSchema> animations;

    /**
     * Creates a new instance of the <code>AnimationInstance</code> class.
     * @param schema <code>AnimationControllerSchema</code>: The schema used to generate this <code>AnimationControllerInstance</code> instance.
     */
    public AnimationControllerInstance(AnimationControllerSchema schema) {

        super(schema);
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

            animations = new HashMap<>();
            for (Identifier animationIdentifier : attributeAnimations) {

                animations.put(animationIdentifier, (AnimationSchema) getSchema(AssetType.ANIMATION, animationIdentifier));
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
