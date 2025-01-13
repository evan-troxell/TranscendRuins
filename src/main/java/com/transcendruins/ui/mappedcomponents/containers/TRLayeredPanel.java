package com.transcendruins.ui.mappedcomponents.containers;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
    public final Set<Map.Entry<String, TRComponent>> getComponentSet() {

        return componentMap.entrySet();
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRComponent> componentEntry : getComponentSet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }

    @Override
    public final void setComponentEnabled(String name, boolean enabled) {

        TRComponent component = getComponent(name);
        component.setEnabled(enabled);
    }
}
