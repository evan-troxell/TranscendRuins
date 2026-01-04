/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.modelassets.entities;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.attack.AttackSchema;
import com.transcendruins.assets.modelassets.entities.EntityAttributes.DoubleDimension;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>EntityInstance</code>: A class representing a generated entity
 * instance.
 */
public final class EntityInstance extends PrimaryAssetInstance {

    private double tileWidth;

    public final double getTileWidth() {

        return tileWidth;
    }

    private double tileLength;

    public final double getTileLength() {

        return tileLength;
    }

    private Vector position = Vector.IDENTITY_VECTOR;

    public final void setPosition(Vector position) {

        this.position = position;
        queueAreaUpdate();
    }

    @Override
    public final void setPosition(int tileX, int tileZ) {

        setPosition(new Vector(tileX, 0.0, tileZ).multiply(World.UNIT_TILE));
    }

    public final void translate(Vector dv) {

        setPosition(position.add(dv));
    }

    @Override
    public final void translate(int dx, int dz) {

        setPosition(position.add(new Vector(dx, 0, dz).multiply(World.UNIT_TILE)));
    }

    private double rotatedTileWidth;

    private double rotatedTileLength;

    private double heading;

    private void updateTileRotation() {

        double theta = Math.toRadians(heading);
        double C = Math.abs(Math.cos(theta));
        double S = Math.abs(Math.sin(theta));

        rotatedTileWidth = C * tileWidth + S * tileLength;
        rotatedTileLength = C * tileLength + S * tileWidth;

        queueAreaUpdate();
    }

    public final void rotate(double degrees) {

        if (degrees == 0) {

            return;
        }

        double dx = rotatedTileWidth;
        double dz = rotatedTileLength;

        heading += degrees;
        heading %= 360;
        updateTileRotation();

        dx -= rotatedTileWidth;
        dz -= rotatedTileLength;

        // Recenter the entity on the previous center.
        translate(new Vector(dx, 0, dz).multiply(World.UNIT_TILE / 2.0));
    }

    @Override
    public final void rotate(int direction, int areaWidth, int areaLength) {

        if (direction == World.EAST) {

            return;
        }

        heading += direction * 90;
        heading %= 360;
        updateTileRotation();

        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        position = switch (direction) {

        case World.NORTH -> new Vector(z, y, areaLength * World.UNIT_TILE - x - rotatedTileLength * World.UNIT_TILE);

        case World.WEST -> new Vector(areaWidth * World.UNIT_TILE - x - rotatedTileWidth * World.UNIT_TILE, y,
                areaLength * World.UNIT_TILE - z - rotatedTileLength * World.UNIT_TILE);

        case World.SOUTH -> new Vector(areaWidth * World.UNIT_TILE - z - rotatedTileWidth * World.UNIT_TILE, y, x);

        default -> position;
        };
    }

    @Override
    public final Vector getPosition() {

        // TODO adjust for tile height
        return position.add(new Vector(rotatedTileWidth / 2.0, 0.0, rotatedTileLength / 2.0).multiply(World.UNIT_TILE));
    }

    @Override
    public final Quaternion getRotation() {

        return Quaternion.fromEulerRotation(Math.toRadians(heading), new Vector(0, 1.0, 0));
    }

    private Rectangle getTileBoundsAt(double left, double top) {

        int minTileX = (int) Math.floor(left);
        int minTileZ = (int) Math.floor(top);

        int maxTileX = (int) Math.ceil(left + rotatedTileWidth);
        int maxTileZ = (int) Math.ceil(top + rotatedTileLength);

        return new Rectangle(minTileX, minTileZ, maxTileX - minTileX, maxTileZ - minTileZ);
    }

    @Override
    protected final Rectangle getInternalTileBounds() {

        return getTileBoundsAt(position.getX() / World.UNIT_TILE, position.getZ() / World.UNIT_TILE);
    }

    @Override
    public final Rectangle getInternalTileBoundsTranslated(int dx, int dz) {

        return getTileBoundsAt(position.getX() / World.UNIT_TILE + dx, position.getZ() / World.UNIT_TILE + dz);
    }

    @Override
    public final Rectangle getInternalTileBoundsAt(int tileX, int tileZ) {

        return getTileBoundsAt((double) tileX, (double) tileZ);
    }

    @Override
    public final void updateArea(AreaGrid area) {

        removeAreaUpdate();
        area.updateEntity(this);
    }

    public final boolean addModelChild(EntityInstance modelChild, String attachment) {

        return super.addModelChild(modelChild, attachment);
    }

    private long attackEnd = -1;

    private boolean attacking = false;

    public final boolean isAttacking() {

        return attacking;
    }

    private ItemInstance mainhandItem;

    private final AttackInstance attack = new AttackInstance();

    @Override
    public final void updateSlot(String name, ItemInstance item) {

        if (InventoryInstance.MAINHAND.equals(name)) {

            mainhandItem = item;
        }
    }

    public final AttackInstance getAttack() {

        return mainhandItem == null ? attack : mainhandItem.getAttack();
    }

    public final void attack(long time, AttackInstance attack, EntityInstance target) {

        if (attackEnd > -1 && time < attackEnd) {

            return;
        }

        attacking = true;
        attackEnd = attack.call(time, target);
    }

    public final void inflict(AttackInstance attack) {
    }

    private String mapIconPath;

    private ImageIcon mapIcon;

    public final ImageIcon getMapIcon() {

        return mapIcon;
    }

    /**
     * Creates a new instance of the <code>EntityInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>EntityInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public EntityInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        EntityContext context = (EntityContext) assetContext;
    }

    @Override
    public void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet) {

        EntityAttributes attributes = (EntityAttributes) attributeSet;

        computeAttribute(attributes.getTileDimensions(), dim -> {

            tileWidth = dim.width();
            tileLength = dim.length();

            updateTileRotation();
        }, attributes, DoubleDimension.DEFAULT);

        computeAttribute(attributes.getAttack(), attack::applyAttributes, attributes, AttackSchema.DEFAULT);

        mapIcon = calculateAttribute(attributes.getMapIcon(), iconPath -> {

            mapIconPath = iconPath;
            return getInstanceTexture(iconPath);
        }, mapIcon, attributes, null);
    }

    @Override
    protected void onPrimaryAssetUpdate(double time) {
    }
}
