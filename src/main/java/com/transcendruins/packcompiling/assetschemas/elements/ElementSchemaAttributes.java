package com.transcendruins.packcompiling.assetschemas.elements;

import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
* <code>ElementSchemaAttributes</code>: A class which represents the attributes of an <code>ElementSchema</code> instance.
*/
public final class ElementSchemaAttributes extends ModelAssetSchemaAttributes {

    /**
     * <code>Boolean</code>: Whether or not the rotation of this <code>ElementSchemaAttributes</code> instance should snap to the four cardinal directions.
     */
    private final Boolean gridRotationSnap;

    /**
     * Compiles this <code>ElementSchemaAttributes</code> instance into a completed instance.
     * @param schema <code>ElementSchema</code>: The schema which created this <code>ElementSchemaAttributes</code> instance.
     * @param schemaJson <code>TracedDictionary</code>: The schema JSON used to compile this <code>ElementSchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this <code>ElementSchemaAttributes</code> instance is the base attribute set of an <code>ElementSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing this <code>ElementSchemaAttributes</code> instance.
     */
    public ElementSchemaAttributes(ElementSchema schema, TracedDictionary schemaJson, boolean isBase) throws LoggedException {

        super(schema, schemaJson, isBase);

        TracedEntry<Boolean> gridRotationSnapEntry = schemaJson.getAsBoolean("gridRotationSnap", true, isBase ? true : null);
        gridRotationSnap = gridRotationSnapEntry.getValue();
    }

    /**
     * Retrieves whether or not the rotation of this <code>ElementSchemaAttributes</code> instance should snap to the four cardinal directions.
     * @return <code>Boolean</code>: The <code>gridRotationSnap</code> field of this <code>ElementSchemaAttributes</code> instance.
     */
    public Boolean getGridRotationSnap() {

        return gridRotationSnap;
    }
}
