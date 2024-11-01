package com.transcendruins.ui.mappedcomponents.containers;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRScreenPanel</code>: A class representing a <code>JPanel</code> with an implemented card layout, whose screens have been mapped, allowing for easy retrieval.
 */
public final class TRScreenPanel extends JPanel implements TRScreenContainer {

    /**
     * <code>String</code>: The name of this <code>TRScreenPanel</code> instance.
     */
    private final String name;

    /**
     * <code>LinkedHashMap&lt;String, TRContainer&gt;</code>: The map of screens of this <code>TRScreenPanel</code> instance.
     */
    private final LinkedHashMap<String, TRContainer> screenMap = new LinkedHashMap<>();

    /**
     * <code>String</code>: The name of the screen currently being displayed, or <code>null</code> if a screen has not yet been assigned.
     */
    private String currentScreen = null;

    /**
     * <code>CardLayout</code>: The layout used to switch between frames of this <code>TRScreenPanel</code> instance.
     */
    private final CardLayout layout = new CardLayout();

    /**
     * Creates a new instance of the <code>TRScreenPanel</code> class.
     * @param name <code>String</code>: The name of this <code>TRScreenPanel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>TRScreenPanel</code> instance.
     */
    public TRScreenPanel(String name, ComponentSettings settings) {

        super();
        this.name = name;

        setLayout(layout);
        applySettings(settings);
    }

    @Override
    public final void addScreen(TRContainer screen) {

        screenMap.put(screen.getComponentName(), screen);
        add((Component) screen, screen.getComponentName());

        if (currentScreen == null) {

            setScreen(screen.getComponentName());
        }
    }

    @Override
    public final void setScreen(String name) {

        currentScreen = name;
        layout.show(this, name);
    }

    @Override
    public final void nextScreen() {

        layout.next(this);
    }

    @Override
    public final TRContainer getScreen(String name) {

        return screenMap.get(name);
    }

    @Override
    public final Set<Map.Entry<String, TRContainer>> getScreenSet() {

        return screenMap.entrySet();
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRContainer> componentEntry : getScreenSet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }

    @Override
    public final String getComponentName() {

        return name;
    }
}
