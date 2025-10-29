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

package com.transcendruins.assets.modelassets.items;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.attack.AttackSchema;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>ItemAttributes</code>: A class which represents the attributes of an
 * <code>ItemSchema</code> instance.
 */
public final class ItemAttributes extends ModelAssetAttributes {

    /**
     * <code>Integer</code>: The stack size of this <code>ItemAttributes</code>
     * instance.
     */
    private final Integer stackSize;

    /**
     * Retrieves the stack size of this <code>ItemAttributes</code> instance.
     * 
     * @return <code>Integer</code>: The <code>stackSize</code> field of this
     *         <code>ItemAttributes</code> instance.
     */
    public final Integer getStackSize() {

        return stackSize;
    }

    private final String icon;

    public final String getIcon() {

        return icon;
    }

    private final AttackSchema attack;

    public final AttackSchema getAttack() {

        return attack;
    }

    /**
     * Compiles this <code>ItemAttributes</code> instance into a completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ItemAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>ItemAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ItemAttributes</code> instance is the base attribute set
     *               of an <code>ItemSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ItemAttributes</code> instance.
     */
    public ItemAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        TracedEntry<Integer> stackSizeEntry = json.getAsInteger("stackSize", true, null);
        stackSize = stackSizeEntry.getValue();

        TracedEntry<String> iconEntry = json.getAsString("icon", !isBase, null);
        icon = iconEntry.getValue();

        attack = AttackSchema.createAttack(json, isBase);
    }
}
