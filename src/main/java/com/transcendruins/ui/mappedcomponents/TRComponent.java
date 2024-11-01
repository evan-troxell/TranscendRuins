package com.transcendruins.ui.mappedcomponents;

import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRComponent</code>: An interface representing any custom component implementing the <code>ComponentSettings</code> settings system.
 */
public interface TRComponent {
    
    /**
     * Appies a set of component settings to this <code>TRComponent</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>TRComponent</code> instance.
     */
    public void applySettings(ComponentSettings settings);

    /**
     * Sets this <code>TRComponent</code> instance to be enabled or disabled.
     * @param enabled <code>boolean</code>: Whether or not this component should be enabled.
     */
    public void setEnabled(boolean enabled);

    /**
     * Retrieves the name of this <code>TRComponent</code> instance.
     * @return <code>String</code>: The name of this <code>TRComponent</code> instance.
     */
    public String getComponentName();
}
