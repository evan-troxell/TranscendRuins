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

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLayeredPane;

import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRLayoutPanel</code>: A class representing a <code>JLayeredPane</code>
 * whose components have been mapped, allowing for easy retrieval.
 */
public class TRLayeredPanel extends JLayeredPane implements TRContainer {

    /**
     * <code>String</code>: The name of this <code>TRLayeredPanel</code> instance.
     */
    private final String name;

    @Override
    public final String getComponentName() {

        return name;
    }

    /**
     * <code>LinkedHashMap&lt;String, TRComponent&gt;</code>: The map of components
     * of this <code>TRLayoutPanel</code> instance.
     */
    private final LinkedHashMap<String, TRComponent> componentMap = new LinkedHashMap<>();

    /**
     * Creates a new instance of the <code>TRLayoutPanel</code> class.
     * 
     * @param name     <code>String</code>: The name of this
     *                 <code>TRLayeredPanel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRLayeredPanel</code> instance.
     */
    public TRLayeredPanel(String name, ComponentSettings settings) {

        super();
        this.name = name;

        applySettings(settings);
    }

    /**
     * Sets the visibility of a component in this <code>TRLayoutPanel</code>
     * instance.
     * 
     * @param name    <code>String</code>: The name of the component whose
     *                visibility to set.
     * @param visible <code>boolean</code>: The visibility state to apply to the
     *                component.
     */
    public final void setComponentVisibility(String name, boolean visible) {

        Component component = (Component) getComponent(name);
        component.setVisible(visible);
    }

    /**
     * Shows a component in this <code>TRLayoutPanel</code> instance.
     * 
     * @param name <code>String</code>: The name of the component to show.
     */
    public final void showComponent(String name) {

        setComponentVisibility(name, true);
    }

    /**
     * Hides a component in this <code>TRLayoutPanel</code> instance.
     * 
     * @param name <code>String</code>: The name of the component to hide.
     */
    public final void hideComponent(String name) {

        setComponentVisibility(name, false);
    }

    @Override
    public final void addComponent(TRComponent component) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component);
    }

    @Override
    public final void addComponent(TRComponent component, int index) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, index);
    }

    @Override
    public final void addComponent(TRComponent component, Object constraints) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, constraints);
    }

    @Override
    public final void addComponent(TRComponent component, Object constraints, int index) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, constraints, index);
    }

    @Override
    public final TRComponent getComponent(String key) {

        return componentMap.get(key);
    }

    @Override
    public final HashMap<String, TRComponent> getTRComponents() {

        return new HashMap<>(componentMap);
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRComponent> componentEntry : getTRComponents().entrySet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }

    @Override
    public final void setComponentEnabled(String name, boolean enabled) {

        TRComponent component = getComponent(name);
        component.setEnabled(enabled);
    }
}
