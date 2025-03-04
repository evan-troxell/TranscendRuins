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

package com.transcendruins.ui.mappedcomponents;

import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRComponent</code>: An interface representing any custom component
 * implementing the <code>ComponentSettings</code> settings system.
 */
public interface TRComponent {

    /**
     * Appies a set of component settings to this <code>TRComponent</code> instance.
     * 
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRComponent</code> instance.
     */
    public void applySettings(ComponentSettings settings);

    /**
     * Sets this <code>TRComponent</code> instance to be enabled or disabled.
     * 
     * @param enabled <code>boolean</code>: Whether or not this component should be
     *                enabled.
     */
    public void setEnabled(boolean enabled);

    /**
     * Retrieves the name of this <code>TRComponent</code> instance.
     * 
     * @return <code>String</code>: The name of this <code>TRComponent</code>
     *         instance.
     */
    public String getComponentName();
}
