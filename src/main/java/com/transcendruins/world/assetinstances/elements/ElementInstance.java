package com.transcendruins.world.assetinstances.elements;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaAttributes;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchema;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchemaAttributes;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.ModelAssetInstance;

/**
 * <code>ElementInstance</code>: A class representing a generated element
 * instance.
 */
public final class ElementInstance extends ModelAssetInstance {

    /**
     * <code>long</code>: The X coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private long tileX;

    /**
     * Retrieves the X coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>long</code>: The <code>tileX</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public long getTileX() {

        return tileX;
    }

    /**
     * <code>long</code>: The Z coordinate of the tile of this
     * <code>ElementInstance</code> instance.
     */
    private long tileZ;

    /**
     * Retrieves the Z coordinate of this <code>ElementInstance</code> instance.
     * 
     * @return <code>long</code>: The <code>tileZ</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public long getTileZ() {

        return tileZ;
    }

    /**
     * <code>Vector</code>: The tile offset of this <code>ElementInstance</code>
     * instance.
     */
    private Vector tileOffset;

    /**
     * Retrieves the tile offset of this <code>ElementInstance</code> instance.
     * 
     * @return <code>Vector</code>: The <code>tileOffset</code> field of this
     *         <code>ElementInstance</code> instance.
     */
    public Vector getTileOffset() {

        return tileOffset;
    }

    /**
     * <code>int</code>: The cardinal direction of this <code>ElementInstance</code>
     * instance, represented by the cardinal direction enums of the
     * <code>World</code> class.
     */
    private int cardinalDirection;

    /**
     * Retrieves the cardinal direction og this <code>ElementInstance</code>
     * instance.
     * 
     * @return <code>int</code>: The <code>cardinalDirection</code> field of this
     *         <code>ElementInstance</code> instance, represented by the cardinal
     *         direction enums of the <code>World</code> class.
     */
    public int getCardinalDirection() {

        return cardinalDirection;
    }

    /**
     * Creates a new instance of the <code>ElementInstance</code> class.
     * 
     * @param schema            <code>ElementSchema</code>: The schema used to
     *                          generate this <code>ElementInstance</code> instance.
     * @param world             <code>World</code>: The world copy to assign to this
     *                          <code>ElementInstance</code> instance.
     * @param tileX             <code>long</code>: The X coordinate of the tile to
     *                          assign to this <code>ElementInstance</code>
     *                          instance.
     * @param tileZ             <code>long</code>: The Z coordinate of the tile to
     *                          assign to this <code>ElementInstance</code>
     *                          instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine
     *                          with the heading, represented by the cardinal
     *                          direction enums of the <code>World</code> class.
     * @param tileOffset        <code>Vector</code>: The tile offset to assign to
     *                          this <code>ElementInstance</code> instance.
     */
    public ElementInstance(ElementSchema schema, World world, long tileX, long tileZ, int cardinalDirection,
            Vector tileOffset) {

        super(schema, world, tileX, tileZ, cardinalDirection, tileOffset);
    }

    @Override
    protected void applyOffset(long newTileX, long newTileZ, int newCardinalDirection, Vector tileOffset) {

        this.tileX = newTileX;
        this.tileZ = newTileZ;
        this.cardinalDirection = newCardinalDirection;

        super.applyOffset(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies a attribute set to this <code>ElementInstance</code> instance.
     * 
     * @param attributeSet <code>AssetSchemaAttributes</code>: The attribute set to
     *                     apply.
     */
    @Override
    protected void applyAttributeSet(AssetSchemaAttributes attributeSet) {

        ElementSchemaAttributes attributes = (ElementSchemaAttributes) attributeSet;

        if (getPositionInitialized())
            applyOffset(tileX, tileZ, cardinalDirection, tileOffset);
    }

    @Override
    protected void update() {}
}
