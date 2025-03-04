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

package com.transcendruins.ui.mappedcomponents.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRButton</code>: A class representing a button.
 */
public abstract class TRButton extends JButton implements TRComponent {

    /**
     * <code>String</code>: The name of this <code>TRButton</code> instance.
     */
    private final String name;

    @Override
    public final String getComponentName() {

        return name;
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRButton</code>
     *                 instance.
     * @param text     <code>String</code>: The text to apply to this
     *                 <code>TRButton</code> instance.
     * @param size     <code>Dimension</code>: The size to apply to this
     *                 <code>TRButton</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRButton</code> instance.
     */
    public TRButton(String name, String text, Dimension size, ComponentSettings settings) {

        this(name, text, null, size, settings);
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRButton</code>
     *                 instance.
     * @param icon     <code>ImageIcon</code>: The icon to apply to this
     *                 <code>TRButton</code> instance.
     * @param size     <code>Dimension</code>: The size to apply to this
     *                 <code>TRButton</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRButton</code> instance.
     */
    public TRButton(String name, ImageIcon icon, Dimension size, ComponentSettings settings) {

        this(name, null, icon, size, settings);
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRButton</code>
     *                 instance.
     * @param text     <code>String</code>: The text to apply to this
     *                 <code>TRButton</code> instance.
     * @param icon     <code>ImageIcon</code>: The icon to apply to this
     *                 <code>TRButton</code> instance.
     * @param size     <code>Dimension</code>: The size to apply to this
     *                 <code>TRButton</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRButton</code> instance.
     */
    public TRButton(String name, String text, ImageIcon icon, Dimension size, ComponentSettings settings) {

        super(text, icon);
        this.name = name;

        setMaximumSize(size);
        setPreferredSize(size);
        addActionListener((ActionEvent _) -> onClick());

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (isEnabled()) {

                    setBackground(settings.getBackground().darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                setBackground(settings.getBackground());
            }

            @Override
            public void mousePressed(MouseEvent e) {

                if (isEnabled()) {

                    setBackground(Color.WHITE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                setBackground(settings.getBackground());
            }
        });

        applySettings(settings);
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class with default
     * settings applied.
     * 
     * @param name <code>String</code>: The name of this <code>TRButton</code>
     *             instance.
     * @param text <code>String</code>: The text to apply to this
     *             <code>TRButton</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this
     *             <code>TRButton</code> instance.
     */
    public TRButton(String name, String text, Dimension size) {

        this(name, text, null, size, ComponentSettings.DEFAULT_BUTTON_SETTINGS);
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class with default
     * settings applied.
     * 
     * @param name <code>String</code>: The name of this <code>TRButton</code>
     *             instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this
     *             <code>TRButton</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this
     *             <code>TRButton</code> instance.
     */
    public TRButton(String name, ImageIcon icon, Dimension size) {

        this(name, null, icon, size, ComponentSettings.DEFAULT_BUTTON_SETTINGS);
    }

    /**
     * Creates a new instance of the <code>TRButton</code> class with default
     * settings applied.
     * 
     * @param name <code>String</code>: The name of this <code>TRButton</code>
     *             instance.
     * @param text <code>String</code>: The text to apply to this
     *             <code>TRButton</code> instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this
     *             <code>TRButton</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this
     *             <code>TRButton</code> instance.
     */
    public TRButton(String name, String text, ImageIcon icon, Dimension size) {

        this(name, text, icon, size, ComponentSettings.DEFAULT_BUTTON_SETTINGS);
    }

    /**
     * The action to execute every click.
     */
    public abstract void onClick();

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
    }
}
