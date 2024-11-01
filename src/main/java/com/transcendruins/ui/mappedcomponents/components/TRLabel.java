package com.transcendruins.ui.mappedcomponents.components;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRLabel</code>: A class representing a label.
 */
public class TRLabel extends JLabel implements TRComponent {

    /**
     * <code>String</code>: The name of this <code>TRLabel</code> instance.
     */
    private final String name;

    /**
     * Creates a new instance of the <code>TRLabel</code> class.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param text <code>String</code>: The text to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, String text, Dimension size, ComponentSettings settings) {

        this(name, text, null, size, settings);
    }

    /**
     * Creates a new instance of the <code>TRLabel</code> class.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, ImageIcon icon, Dimension size, ComponentSettings settings) {

        this(name, null, icon, size, settings);
    }
    
    /**
     * Creates a new instance of the <code>TRLabel</code> class.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param text <code>String</code>: The text to apply to this <code>TRLabel</code> instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, String text, ImageIcon icon, Dimension size, ComponentSettings settings) {

        super(text, icon, SwingConstants.CENTER);
        this.name = name;

        setMaximumSize(size);
        setPreferredSize(size);

        applySettings(settings);
    }

    /**
     * Creates a new instance of the <code>TRLabel</code> class with default settings applied.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param text <code>String</code>: The text to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, String text, Dimension size) {

        this(name, text, null, size);
    }

    /**
     * Creates a new instance of the <code>TRLabel</code> class with default settings applied.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, ImageIcon icon, Dimension size) {

        this(name, null, icon, size);
    }

    /**
     * Creates a new instance of the <code>TRLabel</code> class with default settings applied.
     * @param name <code>String</code>: The name of this <code>TRLabel</code> instance.
     * @param text <code>String</code>: The text to apply to this <code>TRLabel</code> instance.
     * @param icon <code>ImageIcon</code>: The icon to apply to this <code>TRLabel</code> instance.
     * @param size <code>Dimension</code>: The size to apply to this <code>TRLabel</code> instance.
     */
    public TRLabel(String name, String text, ImageIcon icon, Dimension size) {

        this(name, text, icon, size, ComponentSettings.DEFAULT_LABEL_SETTINGS);
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
    }

    @Override
    public final String getComponentName() {

        return name;
    }
}
