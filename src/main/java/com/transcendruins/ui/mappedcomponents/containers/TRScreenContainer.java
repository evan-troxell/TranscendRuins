package com.transcendruins.ui.mappedcomponents.containers;

import java.util.Map;
import java.util.Set;

import com.transcendruins.ui.mappedcomponents.TRComponent;


/**
 * <code>TRScreenContainer</code>: An interface representing a UI container which acts as a card layout, allowing for switching between display panels.
 */
public interface TRScreenContainer extends TRComponent {

    /**
     * Adds a screen to this <code>TRScreenContainer</code> instance, allowing for retrieval later on.
     * @param screen <code>TRContainer</code>: The screen to add.
     */
    public void addScreen(TRContainer screen);

    /**
     * Displays a screen in this <code>TRScreenContainer</code> instance.
     * @param name <code>String</code>: The name of the screen to display.
     */
    public void setScreen(String name);

    /**
     * Displays the next screen in this <code>TRScreenContainer</code> instance.
     */
    public void nextScreen();

    /**
     * Retrieves a screen from this <code>TRScreenContainer</code> instance.
     * @param name <code>String</code>: The name of the screen to retrieve.
     * @return <code>TRContainer</code>: The retrieved screen.
     */
    public TRContainer getScreen(String name);

    /**
     * Retrieves the set of screens contained within this <code>TRScreenContainer</code> instance.
     * @return <code>Set&lt;Map.Entry&lt;String, TRContainer&gt;&gt;</code>: The set of screen entries in this <code>TRScreenContainer</code> instance.
     */
    public Set<Map.Entry<String, TRContainer>> getScreenSet();
}
