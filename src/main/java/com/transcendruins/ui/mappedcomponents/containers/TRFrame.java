package com.transcendruins.ui.mappedcomponents.containers;

import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRFrame</code>: A class representing a <code>JFrame</code> whose
 * screens have been mapped, allowing for easy retrieval.
 */
public class TRFrame extends JFrame implements TRScreenContainer {

    /**
     * <code>String</code>: The name of the standard display panel used in all
     * <code>TRFrame</code> instances.
     */
    public static final String DISPLAY_PANEL_NAME = "displayPanel";

    /**
     * <code>String</code>: The name of this <code>TRFrame</code> instance.
     */
    private final String name;

    @Override
    public final String getComponentName() {

        return name;
    }

    /**
     * <code>TRScreenPanel</code>: The panel used to display all screens of this
     * <code>TRFrame</code> instance.
     */
    private final TRScreenPanel displayPanel;

    /**
     * Creates a new instance of the <code>TRFrame</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRFrame</code>
     *                 instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRFrame</code> instance.
     */
    public TRFrame(String name, ComponentSettings settings) {

        super();
        this.name = name;

        displayPanel = new TRScreenPanel(DISPLAY_PANEL_NAME, settings);
        add(displayPanel);
    }

    @Override
    public final void addScreen(TRContainer screen) {

        displayPanel.addScreen(screen);
    }

    @Override
    public final void setScreen(String name) {

        displayPanel.setScreen(name);
    }

    @Override
    public final void nextScreen() {

        displayPanel.nextScreen();
    }

    @Override
    public final TRContainer getScreen(String name) {

        return displayPanel.getScreen(name);
    }

    @Override
    public final Set<Map.Entry<String, TRContainer>> getScreenSet() {

        return displayPanel.getScreenSet();
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(displayPanel);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRContainer> componentEntry : getScreenSet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }
}
