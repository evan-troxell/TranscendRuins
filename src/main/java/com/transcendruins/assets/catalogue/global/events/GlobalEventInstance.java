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

package com.transcendruins.assets.catalogue.global.events;

import com.transcendruins.world.World;

/**
 * <code>GlobalEventInstance</code>: A class representing an instantiated global
 * map event.
 */
public class GlobalEventInstance {

    /**
     * <code>String</code>: The name of this <code>GlobalEventInstance</code>
     * instance.
     */
    private final String name;

    /**
     * <code>String</code>: The description of this <code>GlobalEventInstance</code>
     * instance.
     */
    private final String description;

    /**
     * Creates a new instance of the <code>GlobalEventInstance</code> class.
     * 
     * @param schema <code>GlobalEventSchema</code>:
     * @param world
     */
    public GlobalEventInstance(GlobalEventSchema schema, World world) {

        name = schema.getName();
        description = schema.getDescription();
    }
}
