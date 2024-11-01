package com.transcendruins.world.assetinstances.elements;

import com.transcendruins.geometry.Position3D;
import com.transcendruins.packcompiling.assetschemas.AssetSchemaComponents;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchema;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchemaComponents;
import com.transcendruins.world.assetinstances.ModelAssetInstance;

/**
 * <code>ElementInstance</code>: A class representing a generated element instance.
 */
public final class ElementInstance extends ModelAssetInstance {

    /**
     * <code>long</code>: The X coordinate of the tile of this <code>ElementInstance</code> instance.
     */
    private long tileX;

    /**
     * <code>long</code>: The Z coordinate of the tile of this <code>ElementInstance</code> instance.
     */
    private long tileZ;

    /**
     * <code>Position3D</code>: The tile offset of this <code>ElementInstance</code> instance.
     */
    private Position3D tileOffset;

    /**
     * <code>int</code>: The cardinal direction of this <code>ElementInstance</code> instance, represented by the cardinal direction enums of the <code>World</code> class.
     */
    private int cardinalDirection;

    /**
     * <code>boolean</code>: Whether or not the rotation of this <code>ElementInstance</code> instance should snap to the four cardinal directions.
     */
    private boolean gridRotationSnap;

    /**
     * Creates a new instance of the <code>ElementInstance</code> class.
     * @param schema <code>ElementSchema</code>: The schema used to generate this <code>ElementInstance</code> instance.
     * @param tileX <code>long</code>: The X coordinate of the tile to assign to this <code>ElementInstance</code> instance.
     * @param tileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>ElementInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>ElementInstance</code> instance.
     */
    public ElementInstance(ElementSchema schema, long tileX, long tileZ, int cardinalDirection, Position3D tileOffset) {

        super(schema, tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Applies a position to this <code>ElementInstance</code> instance.
     * @param newTileX <code>long</code>: The X coordinate of the tile to assign to this <code>ElementInstance</code> instance.
     * @param newTileZ <code>long</code>: The Z coordinate of the tile to assign to this <code>ElementInstance</code> instance.
     * @param cardinalDirection <code>int</code>: The cardinal direction to combine with the heading, represented by the cardinal direction enums of the <code>World</code> class.
     * @param tileOffset <code>Position3D</code>: The tile offset to assign to this <code>ElementInstance</code> instance.
     */
    @Override
    protected void applyPosition(long newTileX, long newTileZ, int newCardinalDirection, Position3D newTileOffset) {

        this.tileX = newTileX;
        this.tileZ = newTileZ;
        this.cardinalDirection = newCardinalDirection;

        double heading = gridRotationSnap ? 0 : newTileOffset.getHeading();

        tileOffset = new Position3D(newTileOffset.getPosition(), heading, newTileOffset.getPitch(), true);

        super.applyPosition(tileX, tileZ, cardinalDirection, tileOffset);
    }

    /**
     * Retrieves the X coordinate of this <code>ElementInstance</code> instance.
     * @return <code>long</code>: The <code>tileX</code> property of this <code>ElementInstance</code> instance.
     */
    public long getTileX() {

        return tileX;
    }

    /**
     * Retrieves the Z coordinate of this <code>ElementInstance</code> instance.
     * @return <code>long</code>: The <code>tileZ</code> property of this <code>ElementInstance</code> instance.
     */
    public long getTileZ() {

        return tileZ;
    }

    /**
     * Retrieves the cardinal direction og this <code>ElementInstance</code> instance.
     * @return <code>int</code>: The <code>cardinalDirection</code> property of this <code>ElementInstance</code> instance, represented by the cardinal direction enums of the <code>World</code> class.
     */
    public int getCardinalDirection() {

        return cardinalDirection;
    }

    /**
     * Retrieves the tile offset of this <code>ElementInstance</code> instance.
     * @return <code>Position3D</code>: The <code>tileOffset</code> property of this <code>ElementInstance</code> instance.
     */
    public Position3D getTileOffset() {

        return tileOffset;
    }

    /**
     * Applies a component set to this <code>ElementInstance</code> instance.
     * @param componentSet <code>AssetSchemaComponents</code>: The component set to apply.
     */
    @Override
    protected void applyComponentSet(AssetSchemaComponents componentSet) {

        ElementSchemaComponents components = (ElementSchemaComponents) componentSet;

        if (components.getGridRotationSnap() != null) {

            gridRotationSnap = components.getGridRotationSnap();
        }

        if (getPositionInitialized()) applyPosition(tileX, tileZ, cardinalDirection, tileOffset);
    }
}
