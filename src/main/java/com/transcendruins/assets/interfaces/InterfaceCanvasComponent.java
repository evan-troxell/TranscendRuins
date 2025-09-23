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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import com.transcendruins.assets.interfaces.InterfaceInstance.ComponentInstance;

public final class InterfaceCanvasComponent extends JPanel {

    private final HashMap<String, InterfaceInstance> menus = new HashMap<>();

    private final ArrayList<String> keys = new ArrayList<>();

    public void addMenu(String key, InterfaceInstance menu) {

        menus.put(key, menu);
        keys.add(key);
    }

    public void removeMenu(String key) {

        menus.remove(key);
        keys.remove(key);
    }

    private InterfaceInstance activeMenu = null;

    public boolean hasActiveMenu() {

        return activeMenu != null;
    }

    public void setActiveMenu(InterfaceInstance activeMenu) {

        this.activeMenu = activeMenu;
    }

    private ComponentInstance hover = null;

    private List<ComponentInstance> hovers = List.of();

    public boolean hasHover() {

        return hover != null;
    }

    private boolean mouseOver = false;

    private boolean mouseDown = false;

    public InterfaceCanvasComponent() {

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

                mouseDown = true;

                press();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                mouseDown = false;

                hover();

                if (hasHover()) {

                    for (ComponentInstance hovered : hovers) {

                        if (!hovered.onPress(hovered.getValue())) {

                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

                mouseOver = true;

                mouseOver(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {

                mouseDown = false;
                mouseOver = false;

                exit();
            }
        });

        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                mouseOver(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                mouseOver(e.getPoint());
            }
        });
    }

    private void exit() {

        // Check if the hover exists.
        if (hasHover()) {

            hover.exit();
            hover = null;

            hovers = List.of();
        }
    }

    private void enter(List<ComponentInstance> hovers) {

        hover = hovers.getFirst();
        this.hovers = hovers;

        if (mouseDown) {

            press();
        } else {

            hover();
        }
    }

    private void hover() {

        // Check if the hover exists.
        if (hasHover()) {

            hover.hover();
        }
    }

    private void press() {

        // Check if the hover exists.
        if (hasHover()) {

            hover.press();
        }
    }

    private void mouseOver(Point point) {

        if (!mouseOver) {

            exit();
        }

        // Retrieve the new focus, or null if the mouse is not over a menu.
        List<ComponentInstance> focus = getHoverAt(point.x, point.y);

        if (focus != hover) {

            exit();
            enter(focus);
        }
    }

    private List<ComponentInstance> getHoverAt(int x, int y) {

        if (hasActiveMenu()) {

            ComponentInstance body = activeMenu.getBody();
            if (body.mouseOver(x, y)) {

                return body.getStackAt(x, y);
            }

            return null;
        }

        for (String key : keys) {

            InterfaceInstance menu = menus.get(key);
            ComponentInstance body = menu.getBody();
            if (body.mouseOver(x, y)) {

                return body.getStackAt(x, y);
            }
        }

        return null;
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        if (hasActiveMenu()) {

            activeMenu.draw(g2d, width, height);
            return;
        }

        for (String key : keys.reversed()) {

            InterfaceInstance menu = menus.get(key);
            menu.draw(g2d, width, height);
        }
    }
}
