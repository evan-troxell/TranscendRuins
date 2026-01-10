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

package com.transcendruins.assets.recipes;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>RecipeAttributes</code>: A class which represents the attributes of a
 * <code>RecipeSchema</code> instance.
 */
public final class RecipeAttributes extends AssetAttributes {

    /**
     * Compiles this <code>RecipeAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>RecipeAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>RecipeAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>RecipeAttributes</code> instance is the base attribute
     *               set of a <code>RecipeAttributes</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>RecipeAttributes</code> instance.
     */
    public RecipeAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);
    }
}
