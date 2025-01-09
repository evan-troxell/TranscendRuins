package com.transcendruins.packcompiling.assetschemas.animationcontrollers;

import java.util.HashSet;

import com.transcendruins.packcompiling.assetschemas.AssetSchemaModules;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.DuplicateIdentifierException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
* <code>AnimationControllerSchemaModules</code>: A class which represents the modules of an <code>AnimationControllerSchema</code> instance.
*/
public final class AnimationControllerSchemaModules extends AssetSchemaModules {

    /**
     * <code>HashSet&lt;Identifier&gt;</code>: The set of all animations of this <code>AnimationControllerSchemaModules</code> instance.
     */
    private final HashSet<Identifier> animations = new HashSet<>();

    /**
     * Compiles this <code>AnimationControllerSchemaModules</code> instance into a completed instance.
     * @param schema <code>AnimationSchema</code>: The schema which created this <code>AnimationControllerSchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>AnimationControllerSchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>AnimationControllerSchemaModules</code> instance is the base module set of an <code>AnimationSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>AnimationControllerSchemaModules</code> instance.
     */
    public AnimationControllerSchemaModules(AnimationControllerSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

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
     * Retrieves the animations of this <code>AnimationSchemaModules</code> instance.
     * @return <code>HashMap&lt;Identifier&gt;</code>: A copy of the <code>timestampsSorted</code> field of this <code>AnimationSchemaModules</code> instance.
     */
    public HashSet<Identifier> getAnimations() {

        return new HashSet<>(animations);
    }
}
