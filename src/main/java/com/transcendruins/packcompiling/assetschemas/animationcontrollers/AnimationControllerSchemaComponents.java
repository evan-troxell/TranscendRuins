package com.transcendruins.packcompiling.assetschemas.animationcontrollers;

import java.util.HashSet;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>AnimationControllerSchemaComponents</code>: A class which represents the components of an <code>AnimationControllerSchema</code> instance.
*/
public final class AnimationControllerSchemaComponents extends AssetSchemaComponents {

    /**
     * <code>HashSet&lt;Identifier&gt;</code>: The set of all animations of this <code>AnimationControllerSchemaComponents</code> instance.
     */
    private final HashSet<Identifier> animations = new HashSet<>();

    /**
     * Compiles this <code>AnimationControllerSchemaComponents</code> instance into a completed instance.
     * @param schema <code>AnimationSchema</code>: The schema which created this <code>AnimationControllerSchemaComponents</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>AnimationControllerSchemaComponents</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>AnimationControllerSchemaComponents</code> instance is the base component set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>AnimationControllerSchemaComponents</code> instance.
     */
    public AnimationControllerSchemaComponents(AnimationControllerSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<TracedArray> animationsEntry = schemaJson.getAsArray("animations", !isBase);
        TracedArray animationsJson = animationsEntry.getValue();

        if (animationsJson != null) {
            for (int i = 0; i < animationsJson.size(); i++) {

                TracedEntry<Identifier> animationIndexEntry = animationsJson.getAsIdentifier(i, false);
                Identifier animationIndex = animationIndexEntry.getValue();
                if (animations.contains(animationIndex)) {

                    throw new DuplicateIdentifierException(animationIndexEntry);
                }

                addElementDependency(AssetType.ANIMATION, animationIndexEntry);
                animations.add(animationIndex);
            }
        }
    }

    /**
     * Retrieves the animations of this <code>AnimationSchemaComponents</code> instance.
     * @return <code>HashMap&lt;Identifier&gt;</code>: A copy of the <code>timestampsSorted</code> field of this <code>AnimationSchemaComponents</code> instance.
     */
    public HashSet<Identifier> getAnimations() {

        return new HashSet<>(animations);
    }
}
