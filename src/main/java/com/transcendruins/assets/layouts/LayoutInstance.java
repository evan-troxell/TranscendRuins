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

package com.transcendruins.assets.layouts;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetInstance;

/**
 * <code>LayoutInstance</code>: A class representing a generated layout
 * instance.
 */
public final class LayoutInstance extends AssetInstance {

    /**
     * Creates a new instance of the <code>LayoutInstance</code> class.
     * 
     * @param context <code>LayoutContext</code>: The context used to generate
     *                this <code>LayoutInstance</code> instance.
     */
    public LayoutInstance(LayoutContext context) {

        super(context);
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        LayoutAttributes attributes = (LayoutAttributes) attributeSet;
    }

    @Override
    protected void onUpdate(double time) {
    }
}
