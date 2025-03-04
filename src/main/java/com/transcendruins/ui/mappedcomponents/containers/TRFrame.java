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

import java.util.HashMap;
import java.util.Map;

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
    public final HashMap<String, TRContainer> getTRScreens() {

        return displayPanel.getTRScreens();
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(displayPanel);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRContainer> componentEntry : getTRScreens().entrySet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }
}
