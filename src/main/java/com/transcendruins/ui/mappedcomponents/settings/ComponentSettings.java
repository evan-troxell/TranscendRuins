package com.transcendruins.ui.mappedcomponents.settings;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * <code>TRButton</code>: A class representing the applied settings of a component.
 */
public final class ComponentSettings {

    /**
     * <code>Color</code>: The color of background components of the UI.
     */
    public static final Color BACKGROUND_COLOR = new Color(96, 96, 96);

    /**
     * <code>Color</code>: The color of foreground components of the UI.
     */
    public static final Color FOREGROUND_COLOR = new Color(128, 128, 128);

    /**
     * <code>Color</code>: The color of palette components of the UI.
     */
    public static final Color PALETTE_COLOR = new Color(192, 192, 192);

    /**
     * <code>Color</code>: The color of overlay components of the UI.
     */
    public static final Color OVERLAY_COLOR = new Color(128, 128, 128, 128);

    /**
     * <code>Color</code>: The default text color of components of the UI.
     */
    public static final Color DEFAULT_TEXT_COLOR = new Color(64, 64, 64);

    /**
     * <code>Color</code>: The text color of overlay components of the UI.
     */
    public static final Color OVERLAY_TEXT_COLOR = new Color(196, 196, 196);

    /**
     * <code>Border</code>: The border of components of the UI.
     */
    public static final Border COMPONENT_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.BLACK);

    /**
     * <code>Font</code>: The default font of components of the UI.
     */
    public static final Font DEFAULT_FONT = new Font("DialogInput", Font.PLAIN, 14);

    /**
     * <code>ComponentSettings</code>: The component settings of a background panel.
     */
    public static final ComponentSettings BACKGROUND_PANEL_SETTINGS = createSettings(BACKGROUND_COLOR, null);

    /**
     * <code>ComponentSettings</code>: The component settings of a foreground panel.
     */
    public static final ComponentSettings FOREGROUND_PANEL_SETTINGS = createSettings(FOREGROUND_COLOR, COMPONENT_BORDER);

    /**
     * <code>ComponentSettings</code>: The component settings of a palette panel.
     */
    public static final ComponentSettings PALETTE_PANEL_SETTINGS = createSettings(PALETTE_COLOR, COMPONENT_BORDER);

    /**
     * <code>ComponentSettings</code>: The component settings of a hidden component.
     */
    public static final ComponentSettings HIDDEN_COMPONENT_SETTINGS = createTransparentSettings(DEFAULT_TEXT_COLOR, DEFAULT_FONT);

    /**
     * <code>ComponentSettings</code>: The default component settings of button.
     */
    public static final ComponentSettings DEFAULT_BUTTON_SETTINGS = createSettings(PALETTE_COLOR, COMPONENT_BORDER, DEFAULT_TEXT_COLOR, DEFAULT_FONT);

    /**
     * <code>ComponentSettings</code>: The settings of an overlay button.
     */
    public static final ComponentSettings OVERLAY_BUTTON_SETTINGS = createSettings(OVERLAY_COLOR, COMPONENT_BORDER, OVERLAY_TEXT_COLOR, DEFAULT_FONT);

    /**
     * <code>ComponentSettings</code>: The default component settings of label.
     */
    public static final ComponentSettings DEFAULT_LABEL_SETTINGS = createSettings(PALETTE_COLOR, null, DEFAULT_TEXT_COLOR, DEFAULT_FONT);

    /**
     * <code>ComponentSettings</code>: The component settings of an overlay label.
     */
    public static final ComponentSettings OVERLAY_LABEL_SETTINGS = createSettings(OVERLAY_COLOR, null, OVERLAY_TEXT_COLOR, DEFAULT_FONT);

    /**
     * <code>Color</code>: The background color of this <code>ComponentSettings</code> instance.
     */
    private final Color background;

    /**
     * <code>Color</code>: The foreground color of this <code>ComponentSettings</code> instance.
     */
    private final Color foreground;

    /**
     * <code>Border</code>: The border of this <code>ComponentSettings</code> instance.
     */
    private final Border border;

    /**
     * <code>Font</code>: The font of this <code>ComponentSettings</code> instance.
     */
    private final Font font;

    /**
     * <code>boolean</code>: Whether or not this <code>ComponentSettings</code> instance is opaque.
     */
    private final boolean opaque;

    /**
     * Creates a transparent instance of the <code>ComponentSettings</code> class.
     * @return <code>ComponentSettings</code>: The generated component settings.
     */
    public static ComponentSettings createTransparentSettings() {

        return new ComponentSettings(null, null, null, null, false);
    }

    /**
     * Creates a transparent instance of the <code>ComponentSettings</code> class.
     * @param foreground <code>Color</code>: The foreground color to apply to the new <code>ComponentSettings</code> instance.
     * @param font <code>Font</code>: The font to apply to the new <code>ComponentSettings</code> instance.
     * @return <code>ComponentSettings</code>: The generated component settings.
     */
    public static ComponentSettings createTransparentSettings(Color foreground, Font font) {

        return new ComponentSettings(null, null, foreground, font, false);
    }

    /**
     * Creates an opaque instance of the <code>ComponentSettings</code> class.
     * @param background <code>Color</code>: The background color to apply to the new <code>ComponentSettings</code> instance.
     * @param border <code>Border</code>: The border to apply to the new <code>ComponentSettings</code> instance.
     * @return <code>ComponentSettings</code>: The generated component settings.
     */
    public static ComponentSettings createSettings(Color background, Border border) {

        return new ComponentSettings(background, border, null, null, true);
    }

    /**
     * Creates an opaque instance of the <code>ComponentSettings</code> class.
     * @param background <code>Color</code>: The background color to apply to the new <code>ComponentSettings</code> instance.
     * @param border <code>Border</code>: The border to apply to the new <code>ComponentSettings</code> instance.
     * @param foreground <code>Color</code>: The foreground color to apply to the new <code>ComponentSettings</code> instance.
     * @param font <code>Font</code>: The font to apply to the new <code>ComponentSettings</code> instance.
     * @return <code>ComponentSettings</code>: The generated component settings.
     */
    public static ComponentSettings createSettings(Color background, Border border, Color foreground, Font font) {

        return new ComponentSettings(background, border, foreground, font, true);
    }
    
    /**
     * Creates a new instance of the <code>ComponentSettings</code> class.
     * @param background <code>Color</code>: The background color to apply to this <code>ComponentSettings</code> instance.
     * @param border <code>Border</code>: The border to apply to this <code>ComponentSettings</code> instance.
     * @param foreground <code>Color</code>: The foreground color to apply to the new <code>ComponentSettings</code> instance.
     * @param font <code>Font</code>: The font to apply to this <code>ComponentSettings</code> instance.
     * @param opaque <code>boolean</code> Whether or not this <code>ComponentSettings</code> instance should be opaque.
     */
    public ComponentSettings(Color background, Border border, Color foreground, Font font, boolean opaque) {

        // The default background color should be applied when one is not specified.
        this.background = (background != null) ? background : FOREGROUND_COLOR;

        // A border should not be required.
        this.border = border;

        // The default foreground color should be applied when one is not specified.
        this.foreground = (foreground != null) ? foreground : DEFAULT_TEXT_COLOR;

        // The default font should be applied when one is not specified.
        this.font = (font != null) ? font : DEFAULT_FONT;

        // The opaqueness should always be applied.
        this.opaque = opaque;
    }

    /**
     * Applies the settings of this <code>ComponentSettings</code> to a component.
     * @param component <code>JComponent</code>: The component to be altered.
     */
    public void apply(JComponent component) {

        component.setBackground(background);

        component.setBorder(border);

        component.setForeground(foreground);

        component.setFont(font);

        component.setOpaque(opaque);
    }

    /**
     * Retrieves the background of this <code>ComponentSettings</code> instance.
     * @return <code>Color</code>: The <code>background</code> field of this <code>ComponentSettings</code> instance.
     */
    public Color getBackground() {

        return background;
    }

    /**
     * Retrieves the border of this <code>ComponentSettings</code> instance.
     * @return <code>Color</code>: The <code>border</code> field of this <code>ComponentSettings</code> instance.
     */
    public Border getBorder() {

        return border;
    }

    /**
     * Retrieves the foregound of this <code>ComponentSettings</code> instance.
     * @return <code>Color</code>: The <code>foreground</code> field of this <code>ComponentSettings</code> instance.
     */
    public Color getForeground() {

        return foreground;
    }

    /**
     * Retrieves the font of this <code>ComponentSettings</code> instance.
     * @return <code>Font</code>: The <code>font</code> field of this <code>ComponentSettings</code> instance.
     */
    public Font getFont() {

        return font;
    }

    /**
     * Retrieves whether or not this <code>ComponentSettings</code> instance is opaque.
     * @return <code>Color</code>: The <code>opaque</code> field of this <code>ComponentSettings</code> instance.
     */
    public boolean getOpaque() {

        return opaque;
    }
}
