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

package com.transcendruins.assets.interfaces;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import com.transcendruins.resources.styles.Style;

public final class InterfaceCanvasComponent extends JPanel {

    private final ArrayList<String> keys = new ArrayList<>();

    private final HashMap<String, InterfaceInstance> menus = new HashMap<>();

    public final void addMenu(String key, InterfaceInstance menu) {

        menus.put(key, menu);
        keys.add(key);
    }

    public final void removeMenu(String key) {

        menus.remove(key);
        keys.remove(key);
    }

    private InterfaceInstance activeMenu = null;

    public final boolean hasActiveMenu() {

        return activeMenu != null;
    }

    public final void setActiveMenu(InterfaceInstance activeMenu) {

        this.activeMenu = activeMenu;
    }

    public final void exitActiveMenu() {

        activeMenu = null;
    }

    private final ArrayList<UIComponent> hovered = new ArrayList<>();

    private final ArrayList<UIComponent> pressed = new ArrayList<>();

    public InterfaceCanvasComponent() {

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                pressed.addAll(getPressedAt(mouseX, mouseY));
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                for (UIComponent component : pressed) {

                    component.release(mouseX, mouseY);
                }

                pressed.clear();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                hovered.addAll(getHoveredAt(mouseX, mouseY));
            }

            @Override
            public void mouseExited(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                for (UIComponent component : hovered) {

                    component.exit(mouseX, mouseY);
                }

                hovered.clear();
            }
        });

        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                moveMouse(mouseX, mouseY);
                dragMouse(mouseX, mouseY);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                int mouseX = e.getX();
                int mouseY = e.getY();

                moveMouse(mouseX, mouseY);
            }
        });

        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

            }
        });
    }

    private synchronized void moveMouse(int mouseX, int mouseY) {

        // Press the new components.
        ArrayList<UIComponent> newPressed = getPressedAt(mouseX, mouseY);

        // Unpress old components that are no longer pressed.
        pressed.removeAll(newPressed);
        for (UIComponent component : pressed) {

            component.release(mouseX, mouseY);
        }

        // Reset the pressed components.
        pressed.clear();
        pressed.addAll(newPressed);
    }

    private synchronized ArrayList<UIComponent> getHoveredAt(int mouseX, int mouseY) {

        ArrayList<UIComponent> components = new ArrayList<>();

        if (hasActiveMenu()) {

            activeMenu.hover(mouseX, mouseY, components);
        } else {

            for (int i = keys.size() - 1; i >= 0; i--) {

                String menuKey = keys.get(i);
                InterfaceInstance menu = menus.get(menuKey);
                menu.hover(mouseX, mouseY, components);
            }
        }

        return components;
    }

    private synchronized void dragMouse(int mouseX, int mouseY) {

        // Hover the new components.
        ArrayList<UIComponent> newHovered = getHoveredAt(mouseX, mouseY);

        // Exit old components that are no longer hovered.
        hovered.removeAll(newHovered);
        for (UIComponent component : hovered) {

            component.exit(mouseX, mouseY);
        }

        // Reset the hovered components.
        hovered.clear();
        hovered.addAll(newHovered);
    }

    private synchronized ArrayList<UIComponent> getPressedAt(int mouseX, int mouseY) {

        ArrayList<UIComponent> components = new ArrayList<>();

        if (hasActiveMenu()) {

            activeMenu.press(mouseX, mouseY, components);
        } else {

            for (int i = keys.size() - 1; i >= 0; i--) {

                String menuKey = keys.get(i);
                InterfaceInstance menu = menus.get(menuKey);
                if (!menu.press(mouseX, mouseY, components)) {

                    break;
                }
            }
        }

        return components;
    }

    @Override
    public final void paint(Graphics g) {

        super.paint(g);

        int width = getWidth();
        int height = getHeight();
        int defaultFontSize = 16;

        if (hasActiveMenu()) {

            activeMenu.render(width, height, defaultFontSize, Style.EMPTY);
            return;
        }

        for (String key : keys) {

            InterfaceInstance menu = menus.get(key);
            menu.render(width, height, defaultFontSize, Style.EMPTY);
        }
    }
}
