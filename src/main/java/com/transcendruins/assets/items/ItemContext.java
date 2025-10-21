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

package com.transcendruins.assets.items;

import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.world.World;

/**
 * <code>ItemContext</code>: A class representing the instantiation context of
 * an item.
 */
public final class ItemContext extends AssetContext {

    /**
     * <code>int</code>: The stack size of this <code>ItemContext</code> instance.
     */
    private final int stackSize;

    /**
     * Retrieves the stack size of this <code>ItemContext</code> instance.
     * 
     * @return <code>int</code>: The <code>stackSize</code> field of this
     *         <code>ItemContext</code> instance.
     */
    public int getStackSize() {

        return stackSize;
    }

    /**
     * Creates a new instance of the <code>ItemContext</code> class.
     * 
     * @param presets   <code>AssetPresets</code>: The presets containing schema and
     *                  instantiation information of this <code>ItemContext</code>
     *                  instance.
     * @param world     <code>World</code>: The world copy of this
     *                  <code>ItemContext</code> instance.
     * @param parent    <code>AssetInstance</code>: The parent to assign to this
     *                  <code>ItermContext</code> instance.
     * @param stackSize <code>int</code>: The stack size of this
     *                  <code>ItemContext</code> instance.
     */
    public ItemContext(AssetPresets presets, World world, AssetInstance parent, int stackSize) {

        super(presets, world, parent);

        this.stackSize = stackSize;
    }

    @Override
    public final ItemInstance instantiate() {

        return (ItemInstance) instantiate(this);
    }
}
