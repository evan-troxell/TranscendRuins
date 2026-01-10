/* Copyright 2026 Evan Troxell
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

package com.transcendruins.assets.interfaces;

import java.util.List;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceInstance.ComponentInstance;
import com.transcendruins.assets.interfaces.InterfaceInstance.GlobalMapComponentInstance.LocationDisplay;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.utilities.files.DataConstants;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.world.World;

/**
 * <code>InterfaceContext</code>: A class representing the instantiation context
 * of an interface.
 */
public final class InterfaceContext extends AssetContext {

    public static final String INVENTORY = "inventory";
    public static final String CRAFTING = "crafting";

    /**
     * <code>long</code>: The id of the player which this
     * <code>InterfaceContext</code> instance is associated with.
     */
    private final long playerId;

    /**
     * Retrieves the id of the player which this <code>InterfaceContext</code>
     * instance is associated with.
     * 
     * @return <code>long</code>: The <code>playerId</code> field of this
     *         <code>InterfaceContext</code> instance.
     */
    public final long getPlayerId() {

        return playerId;
    }

    /**
     * <code>ComponentInstance</code>: The parent component to this
     * <code>InterfaceContext</code> instance.
     */
    private final ComponentInstance componentParent;

    /**
     * Retrieves the parent component to this <code>InterfaceContext</code>
     * instance.
     * 
     * @return <code>ComponentInstance</code>: The <code>componentParent</code>
     *         field of this <code>InterfaceContext</code> instance.
     */
    public final ComponentInstance getComponentParent() {

        return componentParent;
    }

    private List<Object> componentValues = List.of();

    public final ImmutableList<Object> getComponentValues() {

        return new ImmutableList<>(componentValues);
    }

    /**
     * Creates a new instance of the <code>InterfaceContext</code> class.
     * 
     * @param presets         <code>AssetPresets</code>: The presets containing
     *                        schema and instantiation information of this
     *                        <code>InterfaceContext</code> instance.
     * @param world           <code>World</code>: The world copy of this
     *                        <code>InterfaceContext</code> instance.
     * @param parent          <code>AssetInstance</code>: The parent to assign to
     *                        this <code>InterfaceContext</code> instance.
     * @param playerId        <code>long</code>: The id of the player which this
     *                        <code>InterfaceContext</code> instance is associated
     *                        with.
     * @param componentParent <code>ComponentInstance</code>: The parent component
     *                        to assign to this <code>InterfaceContext</code>
     *                        instance.
     */
    public InterfaceContext(AssetPresets presets, World world, AssetInstance parent, long playerId,
            ComponentInstance componentParent) {

        super(presets, world, parent);

        this.playerId = playerId;
        this.componentParent = componentParent;
        this.componentValues = List.of();
    }

    public static final InterfaceContext createGlobalMapContext(World world, AssetInstance parent, long playerId,
            double centerX, double centerY) {

        InterfaceContext context = new InterfaceContext(DataConstants.GLOBAL_MAP_IDENTIFIER, world, parent, playerId,
                null);
        context.componentValues = List.of(centerX, centerY);

        return context;
    }

    public static final InterfaceContext createLocationDisplayContext(World world, AssetInstance parent, long playerId,
            LocationDisplay locationDisplay) {

        InterfaceContext context = new InterfaceContext(DataConstants.LOCATION_DISPLAY_IDENTIFIER, world, parent,
                playerId, null);
        context.componentValues = List.of(locationDisplay);

        return context;
    }

    public static final InterfaceContext createInventoryDisplayContext(World world, AssetInstance parent, long playerId,
            InventoryInstance primaryInventory, InventoryComponentSchema primaryUi,
            InventoryInstance secondaryInventory, InventoryComponentSchema secondaryUi) {

        InterfaceContext context = new InterfaceContext(DataConstants.INVENTORY_DISPLAY_IDENTIFIER, world, parent,
                playerId, null);
        context.componentValues = List.of(primaryInventory, primaryUi, secondaryInventory, secondaryUi);

        return context;
    }

    @Override
    public final InterfaceInstance instantiate() {

        return (InterfaceInstance) instantiate(this);
    }
}
