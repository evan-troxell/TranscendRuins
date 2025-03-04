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

package com.transcendruins.assets;

/**
 * <code>Attributes</code>: A class which represents the attribute blueprints of
 * any structure. This class represents the
 */
public abstract class Attributes {

    /**
     * <code>boolean</code>: Whether or not this <code>Attributes</code> instance is
     * the base attribute set of an <code>Instance</code> instance.
     */
    private final boolean isBase;

    /**
     * Retrieves whether or not this <code>Attributes</code> instance is
     * the base attribute set of an <code>Instance</code> instance.
     * 
     * @return <code>boolean</code>: The <code>isBase</code> field of this
     *         <code>Attributes</code> instance.
     */
    public boolean getIsBase() {

        return isBase;
    }

    /**
     * Creates a new instance of the <code>Attributes</code> class.
     * 
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>Attributes</code> instance is the base
     *               attribute set of an <code>Instance</code> instance.
     */
    public Attributes(boolean isBase) {

        this.isBase = isBase;
    };
}
