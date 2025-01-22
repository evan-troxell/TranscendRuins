package com.transcendruins.packcompiling.assetschemas.entities;

import com.transcendruins.packcompiling.assetschemas.ModelAssetSchemaAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>EntitySchemaAttributes</code>: A class which represents the attributes
 * of an <code>EntitySchema</code> instance.
 */
public final class EntitySchemaAttributes extends ModelAssetSchemaAttributes {

    /**
     * Compiles this <code>EntitySchemaAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>EntitySchema</code>: The schema which created this
     *               <code>EntitySchemaAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>EntitySchemaAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>EntitySchemaAttributes</code> instance is the base
     *               attribute set of an <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>EntitySchemaAttributes</code> instance.
     */
    public EntitySchemaAttributes(EntitySchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        finalizeData();
    }

    @Override
    public void finalizeData() {

    }
}
