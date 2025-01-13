package com.transcendruins.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Set;

import com.transcendruins.settings.GameSettings;
import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.components.TRButton;
import com.transcendruins.ui.mappedcomponents.components.TRLabel;
import com.transcendruins.ui.mappedcomponents.containers.TRPanel;
import com.transcendruins.ui.mappedcomponents.containers.TRScreenPanel;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>SettingsPanel</code>: A class which represents a settings panel in the UI.
 */
public abstract class SettingsPanel extends TRPanel {

    /**
     * <code>Dimension</code>: The default dimensions of a settings box.
     */
    public static final Dimension SETTINGS_DIMENSIONS = new Dimension(900, 525);

    /**
     * Creates a new instance of the <code>SettingsPanel</code> class.
     * @param name <code>String</code>: The name of this <code>SettingsPanel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this <code>SettingsPanel</code> instance.
     * @return <code>TRLayoutPanel</code>: The generated panel.
     */
    public SettingsPanel(String name, ComponentSettings settings) {

        super(name, new BorderLayout(), settings);

        TRPanel headerPanel = new TRPanel("headerPanel", new BorderLayout(), ComponentSettings.PALETTE_PANEL_SETTINGS);

        TRButton exitButton = new TRButton(DisplayFrame.EXIT_BUTTON, "< Settings", new Dimension(95, 35), ComponentSettings.DEFAULT_LABEL_SETTINGS) {

            @Override
            public void onClick() {

                onExit();
            }
        };
        headerPanel.addComponent(exitButton, BorderLayout.WEST);

        TRLabel headerLabel = new TRLabel("settingsHeaderLabel", "", new Dimension(100, 35));
        headerPanel.addComponent(headerLabel, BorderLayout.CENTER);

        TRPanel menuContentPanel = new TRPanel("menuContentpanel", new BorderLayout(), ComponentSettings.HIDDEN_COMPONENT_SETTINGS);

        TRScreenPanel menuDisplayPanel = new TRScreenPanel("menuDisplayPanel", ComponentSettings.HIDDEN_COMPONENT_SETTINGS);
        menuContentPanel.addComponent(menuDisplayPanel, BorderLayout.CENTER);

        ArrayList<TRComponent> menuSelectionPanelComponents = new ArrayList<>();

        Set<String> menuSet = GameSettings.getMenuSet();
        for (String menu : menuSet) {

            menuDisplayPanel.addScreen(createMenu(menu));

            TRButton menuSelectorButton = new TRButton(menu + "Button", menu, new Dimension(100, 35)) {


                @Override
                public void onClick() {

                    headerLabel.setText(menu);
                    menuDisplayPanel.setScreen(menu);
                }
            };

            menuSelectionPanelComponents.add(menuSelectorButton);
        }

        TRPanel menuSelectionPanel = TRPanel.createBoxPanel("menuSelectionPanel", menuSelectionPanelComponents, true, LEFT_ALIGNMENT, 15, ComponentSettings.BACKGROUND_PANEL_SETTINGS, false);
        menuContentPanel.addComponent(menuSelectionPanel, BorderLayout.WEST);

        addComponent(headerPanel, BorderLayout.NORTH);
        addComponent(menuContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a settings menu in this <code>SettingsPanel</code> instance.
     * @param name <code>String</code>: The name of the settings menu to create.
     * @return <code>TRPanel</code>: The generated panel.
     */
    private TRPanel createMenu(String name) {

        return switch (name) {

            case GameSettings.VIDEO -> {

                ArrayList<TRComponent> videoComponents = new ArrayList<>();
                TRPanel videoPanel = TRPanel.createBoxPanel(name, videoComponents, true, LEFT_ALIGNMENT, 5, ComponentSettings.HIDDEN_COMPONENT_SETTINGS, false);
                videoPanel.addComponent(new TRLabel("test", "Video", new Dimension(100, 35), ComponentSettings.DEFAULT_LABEL_SETTINGS));

                yield videoPanel;
            }

            case GameSettings.AUDIO -> {

                ArrayList<TRComponent> audioComponents = new ArrayList<>();
                TRPanel audioPanel = TRPanel.createBoxPanel(name, audioComponents, true, LEFT_ALIGNMENT, 5, ComponentSettings.HIDDEN_COMPONENT_SETTINGS, false);
                audioPanel.addComponent(new TRLabel("test", "Audio", new Dimension(100, 35), ComponentSettings.DEFAULT_LABEL_SETTINGS));

                yield audioPanel;
            }

            case GameSettings.UI -> {

                ArrayList<TRComponent> uiComponents = new ArrayList<>();
                TRPanel uiPanel = TRPanel.createBoxPanel(name, uiComponents, true, LEFT_ALIGNMENT, 5, ComponentSettings.HIDDEN_COMPONENT_SETTINGS, false);
                uiPanel.addComponent(new TRLabel("test", "UI", new Dimension(100, 35), ComponentSettings.DEFAULT_LABEL_SETTINGS));

                yield uiPanel;
            }

            default -> null;
        };
    }

    public abstract void onExit();
}
