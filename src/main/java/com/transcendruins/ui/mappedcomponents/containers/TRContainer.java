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

package com.transcendruins.ui.mappedcomponents.containers;

import java.util.Map;

import com.transcendruins.ui.mappedcomponents.TRComponent;

/**
 * <code>TRContainer</code>: An interface representing a UI container whose
 * components have been mapped, allowing for easy retrieval.
 */
public interface TRContainer extends TRComponent {

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for
     * retrieval later on.
     * 
     * @param component <code>TRComponent</code>: The component to add.
     */
    public void addComponent(TRComponent component);

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for
     * retrieval later on.
     * 
     * @param component <code>TRComponent</code>: The component to add.
     * @param index     <code>int</code>: The index at which to insert the
     *                  component.
     */
    public void addComponent(TRComponent component, int index);

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for
     * retrieval later on.
     * 
     * @param component   <code>TRComponent</code>: The component to add.
     * @param constraints <code>Object</code>: The constraints with which to insert
     *                    the component.
     */
    public void addComponent(TRComponent component, Object constraints);

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for
     * retrieval later on.
     * 
     * @param component   <code>TRComponent</code>: The component to add.
     * @param constraints <code>Object</code>: The constraints with which to insert
     *                    the component.
     * @param index       <code>int</code>: The index at which to insert the
     *                    component.
     */
    public void addComponent(TRComponent component, Object constraints, int index);

    /**
     * Retrieves a component from this <code>TRContainer</code> instance.
     * 
     * @param key <code>String</code>: The name of the component to retrieve.
     * @return <code>TRComponent</code>: The retrieved component.
     */
    public TRComponent getComponent(String key);

    /**
     * Sets a component in this <code>TRContainer</code> instance to be enabled or
     * disabled.
     * 
     * @param name    <code>String</code>: The name of the component to enable or
     *                disable.
     * @param enabled <code>boolean</code>: Whether or not the component should be
     *                enabled.
     */
    public void setComponentEnabled(String name, boolean enabled);

    /**
     * Retrieves the set of components contained within this
     * <code>TRContainer</code> instance.
     * 
     * @return <code>Map&lt;String, TRComponent&gt;&gt;</code>: The retrieved map of
     *         this <code>TRContainer</code> instance.
     */
    public Map<String, TRComponent> getTRComponents();
}
