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
 * <code>TRScreenContainer</code>: An interface representing a UI container
 * which acts as a card layout, allowing for switching between display panels.
 */
public interface TRScreenContainer extends TRComponent {

    /**
     * Adds a screen to this <code>TRScreenContainer</code> instance, allowing for
     * retrieval later on.
     * 
     * @param screen <code>TRContainer</code>: The screen to add.
     */
    public void addScreen(TRContainer screen);

    /**
     * Displays a screen in this <code>TRScreenContainer</code> instance.
     * 
     * @param name <code>String</code>: The name of the screen to display.
     */
    public void setScreen(String name);

    /**
     * Displays the next screen in this <code>TRScreenContainer</code> instance.
     */
    public void nextScreen();

    /**
     * Retrieves a screen from this <code>TRScreenContainer</code> instance.
     * 
     * @param name <code>String</code>: The name of the screen to retrieve.
     * @return <code>TRContainer</code>: The retrieved screen.
     */
    public TRContainer getScreen(String name);

    /**
     * Retrieves the set of screens contained within this
     * <code>TRScreenContainer</code> instance.
     * 
     * @return <code>Map&lt;String, TRContainer&gt;</code>: The set of screen
     *         entries in this <code>TRScreenContainer</code> instance.
     */
    public Map<String, TRContainer> getTRScreens();
}
