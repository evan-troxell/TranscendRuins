package com.transcendruins.packcompiling.assetschemas.elements;

import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaModules;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
* <code>ElementSchemaModules</code>: A class which represents the modules of an <code>ElementSchema</code> instance.
*/
public final class ElementSchemaModules extends ModelAssetSchemaModules {

    /**
     * <code>Boolean</code>: Whether or not the rotation of this <code>ElementSchemaModules</code> instance should snap to the four cardinal directions.
     */
    private final Boolean gridRotationSnap;

    /**
     * Compiles this <code>ElementSchemaModules</code> instance into a completed instance.
     * @param schema <code>ElementSchema</code>: The schema which created this <code>ElementSchemaModules</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ElementSchemaModules</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ElementSchemaModules</code> instance is the base module set of an <code>ElementSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ElementSchemaModules</code> instance.
     */
    public ElementSchemaModules(ElementSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<Boolean> gridRotationSnapEntry = schemaJson.getAsBoolean("gridRotationSnap", true, isBase ? true : null);
        gridRotationSnap = gridRotationSnapEntry.getValue();
    }

    /**
     * Retrieves whether or not the rotation of this <code>ElementSchemaModules</code> instance should snap to the four cardinal directions.
     * @return <code>Boolean</code>: The <code>gridRotationSnap</code> field of this <code>ElementSchemaModules</code> instance.
     */
    public Boolean getGridRotationSnap() {

        return gridRotationSnap;
    }
}
