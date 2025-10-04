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

package com.transcendruins.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;

import com.transcendruins.graphics3d.Camera3D;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.rendering.Render3DPanel;
import com.transcendruins.save.GameSettings;
import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.components.TRButton;
import com.transcendruins.ui.mappedcomponents.components.TRLabel;
import com.transcendruins.ui.mappedcomponents.containers.TRFrame;
import com.transcendruins.ui.mappedcomponents.containers.TRLayeredPanel;
import com.transcendruins.ui.mappedcomponents.containers.TRPanel;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;
import com.transcendruins.utilities.files.DataConstants;

/**
 * <code>DisplayFrame</code>: A class representing the display frame used
 * throughout the project.
 */
public final class DisplayFrame extends TRFrame {

    /**
     * <code>String</code>: The name of the main screen.
     */
    public static final String MAIN_SCREEN = "mainScreen";

    /**
     * <code>String</code>: The name of the worlds screen.
     */
    public static final String WORLDS_SCREEN = "worldsScreen";

    /**
     * <code>String</code>: The name of the render display screen.
     */
    public static final String RENDER_DISPLAY_SCREEN = "renderDisplayScreen";

    /**
     * <code>String</code>: The name of the menu panel.
     */
    public static final String MENU_PANEL = "menuPanel";

    /**
     * <code>String</code>: The name of the exit button.
     */
    public static final String EXIT_BUTTON = "exitButton";

    /**
     * <code>String</code>: The name of the settings panel.
     */
    public static final String SETTINGS_PANEL = "settingsPanel";

    /**
     * <code>String</code>: The name of the header panel.
     */
    public static final String HEADER_PANEL = "headerPanel";

    /**
     * Creates a new instance of the <code>DisplayFrame</code> class.
     */
    public DisplayFrame(Camera3D camera) {

        super("displayFrame", ComponentSettings.BACKGROUND_PANEL_SETTINGS);

        ImageIcon icon = DataConstants.FRAME_ICON_PATH.retrieveImage();
        if (icon != null)
            setIconImage(icon.getImage());
        setName("Transcend Ruins V2");
        // setUndecorated(true);

        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        // setResizable(false);

        addScreen(createMainScreen());
        addScreen(createWorldsScreen());
        addScreen(createRenderDisplayScreen(camera));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Creates the main screen.
     * 
     * @return <code>TRLayoutPanel</code>: The generated panel.
     */
    private TRLayeredPanel createMainScreen() {

        TRLayeredPanel mainScreen = new TRLayeredPanel(MAIN_SCREEN, ComponentSettings.BACKGROUND_PANEL_SETTINGS);
        Dimension buttonSize = new Dimension(500, 50);

        // region Menu Panel
        ArrayList<TRComponent> mainScreenMenuPanelComponents = new ArrayList<>();
        TRButton playButton = new TRButton("playButton", "Play", buttonSize) {

            @Override
            public void onClick() {

                setScreen(RENDER_DISPLAY_SCREEN);// setScreen(WORLDS_SCREEN);
            }
        };

        TRButton settingsButton = new TRButton("settingsButton", "Settings", buttonSize) {

            @Override
            public void onClick() {

                mainScreen.showComponent(SETTINGS_PANEL);
                mainScreen.setComponentEnabled(MENU_PANEL, false);
            }
        };

        TRButton quitButton = new TRButton("quitButton", "Quit", buttonSize) {

            @Override
            public void onClick() {

                System.exit(0);
            }
        };

        mainScreenMenuPanelComponents.add(playButton);
        mainScreenMenuPanelComponents.add(settingsButton);
        mainScreenMenuPanelComponents.add(quitButton);

        TRPanel mainScreenMenuPanel = TRPanel.createBoxPanel(MENU_PANEL, mainScreenMenuPanelComponents, true,
                CENTER_ALIGNMENT, 10, ComponentSettings.FOREGROUND_PANEL_SETTINGS, true);
        mainScreenMenuPanel.setBounds(0, 0, getWidth(), getHeight());

        mainScreen.addComponent(mainScreenMenuPanel, JLayeredPane.DEFAULT_LAYER);
        // endregion

        // region Settings Panel
        TRPanel mainScreenSettingsPanel = new SettingsPanel(SETTINGS_PANEL, ComponentSettings.PALETTE_PANEL_SETTINGS) {

            @Override
            public void onExit() {

                mainScreen.hideComponent(SETTINGS_PANEL);
                mainScreen.setComponentEnabled(MENU_PANEL, true);
            }
        };
        mainScreenSettingsPanel.setBounds(getCenteredBounds(SettingsPanel.SETTINGS_DIMENSIONS));

        mainScreen.addComponent(mainScreenSettingsPanel, JLayeredPane.PALETTE_LAYER);
        mainScreen.hideComponent(SETTINGS_PANEL);
        // endregion

        return mainScreen;
    }

    /**
     * Creates the worlds screen.
     * 
     * @return <code>TRPanel</code>: The generated panel.
     */
    private TRPanel createWorldsScreen() {

        ArrayList<TRComponent> worldsScreenComponent = new ArrayList<>();

        TRPanel worldsScreen = TRPanel.createBoxPanel(WORLDS_SCREEN, worldsScreenComponent, true, LEFT_ALIGNMENT, 5,
                ComponentSettings.BACKGROUND_PANEL_SETTINGS, false);

        return worldsScreen;
    }

    /**
     * Creates the render display screen.
     * 
     * @return <code>GraphicsPanel</code>: The generated panel.
     */
    private Render3DPanel createRenderDisplayScreen(Camera3D camera) {

        ArrayList<TRComponent> statsPanelComponents = new ArrayList<>();

        Dimension statsPanelComponentDimensions = new Dimension(150, 35);

        TRLabel fpsDisplayLabel = new TRLabel("fpsDiplayLabel", "Current FPS: ##", statsPanelComponentDimensions,
                ComponentSettings.OVERLAY_LABEL_SETTINGS);

        statsPanelComponents.add(fpsDisplayLabel);

        TRPanel statsPanel = TRPanel.createBoxPanel("statsPanel", statsPanelComponents, true, LEFT_ALIGNMENT, 5,
                ComponentSettings.HIDDEN_COMPONENT_SETTINGS, false);
        statsPanel.setBounds(0, 0, 150, getHeight());

        Render3DPanel renderDisplayScreen = new Render3DPanel(RENDER_DISPLAY_SCREEN) {

            private final int[] mousePosition = new int[2];

            private final HashMap<Integer, Boolean> keysDown = new HashMap<>();

            private boolean getKey(int key) {

                return keysDown.getOrDefault(key, false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

                mousePosition[0] = e.getX();
                mousePosition[1] = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                keysDown.put(e.getKeyCode(), true);

                double x = 0;
                double y = 0;

                if (getKey(KeyEvent.VK_W) || getKey(KeyEvent.VK_UP)) {

                    x += 1;
                }

                if (getKey(KeyEvent.VK_S) || getKey(KeyEvent.VK_DOWN)) {

                    x -= 1;
                }

                if (getKey(KeyEvent.VK_A) || getKey(KeyEvent.VK_LEFT)) {

                    y -= 1;
                }

                if (getKey(KeyEvent.VK_D) || getKey(KeyEvent.VK_RIGHT)) {

                    y += 1;
                }

                // If no keys are pressed, don't transform the camera.
                if (x == 0 && y == 0) {

                    return;
                }

                double angle = Math.atan2(y, x);
                double a = 2;

                camera.transformBy(new Vector(a * Math.sin(camera.getHeading() + angle), 0,
                        -a * Math.cos(camera.getHeading() + angle)));
            }

            @Override
            public void keyReleased(KeyEvent e) {

                keysDown.put(e.getKeyCode(), false);
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                double fovSensitivity = (double) GameSettings.getValue(GameSettings.VIDEO, "fovSensitivity");
                camera.rotateBy((e.getX() - mousePosition[0]) * fovSensitivity,
                        (e.getY() - mousePosition[1]) * fovSensitivity, false);
                mousePosition[0] = e.getX();
                mousePosition[1] = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                camera.zoomBy(((boolean) GameSettings.getValue(GameSettings.VIDEO, "invertZoom") ? -1 : 1)
                        * e.getPreciseWheelRotation() / 20);
            }

            @Override
            public void outputFPS(int outputFPS) {

                fpsDisplayLabel.setText("Current FPS: " + outputFPS);
            }
        };

        TRPanel uiOverlayPanel = new TRPanel("uiOverlayPanel", new BorderLayout(),
                ComponentSettings.HIDDEN_COMPONENT_SETTINGS);
        uiOverlayPanel.setBounds(0, 0, getWidth(), getHeight());

        ArrayList<TRComponent> menuPanelComponents = new ArrayList<>();
        Dimension menuPanelComponentDimensions = new Dimension(80, 50);

        TRButton chatButton = new TRButton("chatButton", "Chat", menuPanelComponentDimensions,
                ComponentSettings.OVERLAY_BUTTON_SETTINGS) {

            @Override
            public void onClick() {
            }
        };

        TRButton menuButton = new TRButton("menuButton", "Menu", menuPanelComponentDimensions,
                ComponentSettings.OVERLAY_BUTTON_SETTINGS) {

            @Override
            public void onClick() {
            }
        };

        TRButton testButton = new TRButton("settingsButton", "Settings", menuPanelComponentDimensions,
                ComponentSettings.OVERLAY_BUTTON_SETTINGS) {

            @Override
            public void onClick() {

                renderDisplayScreen.showComponent(SETTINGS_PANEL);
                renderDisplayScreen.setComponentEnabled("uiOverlayPanel", false);
            }
        };

        menuPanelComponents.add(chatButton);
        menuPanelComponents.add(menuButton);
        menuPanelComponents.add(testButton);

        TRPanel menuPanel = TRPanel.createBoxPanel(MENU_PANEL, menuPanelComponents, false, CENTER_ALIGNMENT, 5,
                ComponentSettings.HIDDEN_COMPONENT_SETTINGS, true);

        uiOverlayPanel.addComponent(menuPanel, BorderLayout.NORTH);

        SettingsPanel settingsPanel = new SettingsPanel(SETTINGS_PANEL, ComponentSettings.PALETTE_PANEL_SETTINGS) {

            @Override
            public void onExit() {

                renderDisplayScreen.hideComponent(SETTINGS_PANEL);
                renderDisplayScreen.setComponentEnabled("uiOverlayPanel", true);
            }

        };
        settingsPanel.setBounds(getCenteredBounds(SettingsPanel.SETTINGS_DIMENSIONS));

        renderDisplayScreen.addComponent(uiOverlayPanel, JLayeredPane.DEFAULT_LAYER);
        renderDisplayScreen.addComponent(statsPanel, JLayeredPane.DEFAULT_LAYER);

        renderDisplayScreen.addComponent(settingsPanel, JLayeredPane.PALETTE_LAYER);
        renderDisplayScreen.hideComponent(SETTINGS_PANEL);

        return renderDisplayScreen;
    }

    public Rectangle getCenteredBounds(Dimension dimensions) {

        return new Rectangle(new Point((int) (getWidth() - dimensions.getWidth()) / 2,
                (int) (getHeight() - dimensions.getHeight()) / 2), dimensions);
    }
}
