package com.transcendruins.ui.mappedcomponents.containers;

import java.util.Map;
import java.util.Set;

import com.transcendruins.ui.mappedcomponents.TRComponent;

/**
 * <code>TRContainer</code>: An interface representing a UI container whose components have been mapped, allowing for easy retrieval.
 */
public interface TRContainer extends TRComponent {

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for retrieval later on.
     * @param component <code>TRComponent</code>: The component to add.
     */
    public void addComponent(TRComponent component);

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for retrieval later on.
     * @param component <code>TRComponent</code>: The component to add.
     * @param index <code>int</code>: The index at which to insert the component.
     */
    public void addComponent(TRComponent component, int index);

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for retrieval later on.
     * @param component <code>TRComponent</code>: The component to add.
     * @param constraints <code>Object</code>: The constraints with which to insert the component.
     */
    public void addComponent(TRComponent component, Object constraints) ;

    /**
     * Adds a component to this <code>TRContainer</code> instance, allowing for retrieval later on.
     * @param component <code>TRComponent</code>: The component to add.
     * @param constraints <code>Object</code>: The constraints with which to insert the component.
     * @param index <code>int</code>: The index at which to insert the component.
     */
    public void addComponent(TRComponent component, Object constraints, int index);

    /**
     * Retrieves a component from this <code>TRContainer</code> instance.
     * @param key <code>String</code>: The name of the component to retrieve.
     * @return <code>TRComponent</code>: The retrieved component.
     */
    public TRComponent getComponent(String key);

    /**
     * Sets a component in this <code>TRContainer</code> instance to be enabled or disabled.
     * @param name <code>String</code>: The name of the component to enable or disable.
     * @param enabled <code>boolean</code>: Whether or not the component should be enabled.
     */
    public void setComponentEnabled(String name, boolean enabled);

    /**
     * Retrieves the set of components contained within this <code>TRContainer</code> instance.
     * @return <code>Set&lt;Map.Entry&lt;String, TRComponent&gt;&gt;</code>: The retrieved entry set of this <code>TRContainer</code> instance.
     */
    public Set<Map.Entry<String, TRComponent>> getComponentSet();
}
