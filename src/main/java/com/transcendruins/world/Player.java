package com.transcendruins.world;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.entities.EntityInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceContext;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.assets.interfaces.InterfaceInstance.GlobalMapComponentInstance.LocationDisplay;
import com.transcendruins.assets.interfaces.UIComponent;
import com.transcendruins.assets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.resources.styles.Style;

public final class Player {

    private final long playerId;

    public final long getPlayerId() {

        return playerId;
    }

    private final EntityInstance entity;

    public final EntityInstance getEntity() {

        return entity;
    }

    private String location;

    private double globalMapX;

    private double globalMapY;

    public final void setGlobalMapCoordinates(Point2D coordinates) {

        synchronized (LOCATION_LOCK) {

            this.globalMapX = coordinates.getX();
            this.globalMapY = coordinates.getY();
        }
    }

    public final Point2D getGlobalMapCoordinates() {

        synchronized (LOCATION_LOCK) {

            return new Point2D.Double(globalMapX, globalMapY);
        }
    }

    private final Object LOCATION_LOCK = new Object();

    public final void setLocation(String location) {

        synchronized (LOCATION_LOCK) {

            this.location = location;
        }
    }

    public final String getLocation() {

        synchronized (LOCATION_LOCK) {

            return location;
        }
    }

    private final Object UI_LOCK = new Object();

    private final ArrayList<InterfaceInstance> uiPanels = new ArrayList<>();

    public final void setPanels(List<AssetPresets> interfacePresets) {

        replacePanels(interfacePresets.stream()
                .map(presets -> new InterfaceContext(presets, entity.getWorld(), entity, playerId, null)).toList());

    }

    private boolean onGlobalMap;

    public final boolean onGlobalMap() {

        synchronized (UI_LOCK) {

            return onGlobalMap;
        }
    }

    public final void enterGlobalMap() {

        synchronized (UI_LOCK) {

            if (onGlobalMap) {

                return;
            }

            onGlobalMap = true;
            InterfaceContext globalMapContext = InterfaceContext.createGlobalMapContext(entity.getWorld(), entity,
                    playerId, globalMapX, globalMapY);
            uiPanels.addFirst((InterfaceInstance) globalMapContext.instantiate());

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    public final void exitGlobalMap() {

        synchronized (UI_LOCK) {

            if (!onGlobalMap) {

                return;
            }

            onGlobalMap = false;
            uiPanels.removeFirst();

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    public final void displayLocation(String location, LocationDisplay locationDisplay) {

        InterfaceContext context = InterfaceContext.createLocationDisplayContext(entity.getWorld(), entity, playerId,
                location, locationDisplay);
        replacePanels(List.of(context));
    }

    public final void displayInventory(PrimaryAssetInstance other) {

        InventoryInstance secondaryInventory = other.getInventory();
        InventoryComponentSchema secondaryUi = other.getInventoryUi();

        InventoryInstance primaryInventory = entity.getInventory();
        InventoryComponentSchema primaryUi = entity.getPrivateInventoryUi();

        if (primaryUi == null || secondaryUi == null) {

            return;
        }

        InterfaceContext context = InterfaceContext.createInventoryDisplayContext(entity.getWorld(), entity, playerId,
                primaryInventory, primaryUi, secondaryInventory, secondaryUi);
        replacePanels(List.of(context));
    }

    private void replacePanels(List<InterfaceContext> contexts) {

        synchronized (UI_LOCK) {

            InterfaceInstance globalMap = onGlobalMap ? uiPanels.getFirst() : null;

            pressFocus = null;

            uiPanels.clear();
            if (onGlobalMap) {

                uiPanels.add(globalMap);
            }
            contexts.forEach(context -> uiPanels.add((InterfaceInstance) context.instantiate()));

            hovered.clear();
            pressed.clear();

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    public final void updateUiPanels(double time) {

        synchronized (UI_LOCK) {

            for (InterfaceInstance panel : uiPanels) {

                panel.update(time);
            }

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    private int screenWidth = 1;

    private int screenHeight = 1;

    public final Dimension getScreenSize() {

        return new Dimension(screenWidth, screenHeight);
    }

    public final void setScreenSize(int width, int height) {

        synchronized (UI_LOCK) {

            if (screenWidth == width && screenHeight == height) {

                return;
            }

            screenWidth = width;
            screenHeight = height;

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    private void calculateSize() {

        for (InterfaceInstance panel : uiPanels) {

            resizePanel(panel);
        }
    }

    private void resizePanel(InterfaceInstance panel) {

        panel.renderBounds(screenWidth, screenHeight, 16, Style.EMPTY);
        panel.rescale(screenWidth, screenHeight);
    }

    private int mouseX = -1;

    private int mouseY = -1;

    private final ArrayList<UIComponent> hovered = new ArrayList<>();

    public final void setMousePosition(int x, int y) {

        synchronized (UI_LOCK) {

            if (mouseX == x && mouseY == y) {

                return;
            }

            mouseX = x;
            mouseY = y;

            calculateSize();
            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    private void calculateHovered() {

        long timestamp = System.currentTimeMillis();

        ArrayList<UIComponent> newHovered = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            if (!panel.hover(mouseX, mouseY, newHovered, timestamp)) {

                break;
            }
        }

        hovered.removeAll(newHovered);
        exitAll();
        hovered.addAll(newHovered);
    }

    private void exitAll() {

        long timestamp = System.currentTimeMillis();

        for (UIComponent exited : hovered) {

            exited.onExit(mouseX, mouseY, timestamp);
        }

        hovered.clear();
    }

    private boolean mousePressed;

    private boolean mouseJustPressed;
    private boolean mouseJustReleased;

    private final ArrayList<UIComponent> pressed = new ArrayList<>();

    public final void setMousePress(boolean pressed) {

        synchronized (UI_LOCK) {

            if (mousePressed == pressed) {

                return;
            }

            mousePressed = pressed;

            if (pressed) {

                mouseJustPressed = true;
            } else {

                mouseJustReleased = true;
            }

            // If the mouse is not being clicked, calculate hover and press states.
            if (pressed) {

                calculatePressed();
                calculateTriggerPress();
            } else {

                releaseAll();
                calculateTriggerRelease();
            }
        }
    }

    private void calculatePressed() {

        long timestamp = System.currentTimeMillis();

        ArrayList<UIComponent> newPressed = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            if (!panel.press(mouseX, mouseY, newPressed, timestamp)) {

                break;
            }
        }

        pressed.removeAll(newPressed);
        releaseAll();
        pressed.addAll(newPressed);
    }

    private void releaseAll() {

        long timestamp = System.currentTimeMillis();

        for (UIComponent exited : pressed) {

            exited.onRelease(mouseX, mouseY, timestamp);
        }

        pressed.clear();
    }

    private UIComponent pressFocus = null;

    private void calculateTriggerPress() {

        pressFocus = null;
        long timestamp = System.currentTimeMillis();

        ArrayList<UIComponent> clicked = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            pressFocus = panel.triggerPress(mouseX, mouseY, clicked, timestamp);

            if (pressFocus != null) {

                break;
            }
        }
    }

    private void calculateTriggerRelease() {

        long timestamp = System.currentTimeMillis();

        ArrayList<UIComponent> clicked = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            if (panel.triggerRelease(mouseX, mouseY, clicked, timestamp) != null) {

                break;
            }
        }
    }

    public final void mouseScroll(int dx, int dy) {

        if (dx == 0 && dy == 0) {

            return;
        }

        long timestamp = System.currentTimeMillis();
        Point displacement = new Point(dx, dy);

        ArrayList<UIComponent> scrolled = new ArrayList<>();

        synchronized (UI_LOCK) {
            for (int i = uiPanels.size() - 1; i >= 0; i--) {

                InterfaceInstance panel = uiPanels.get(i);

                if (!panel.scroll(mouseX, mouseY, displacement, scrolled, timestamp)) {

                    resizePanel(panel);
                    break;
                }

                resizePanel(panel);
            }

            calculateHovered();
            if (mousePressed) {

                calculatePressed();
            }
        }
    }

    public final BufferedImage renderUi() {

        synchronized (UI_LOCK) {

            BufferedImage render = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = render.createGraphics();

            for (InterfaceInstance panel : uiPanels) {

                resizePanel(panel);
                g2d.drawImage(panel.render(), 0, 0, null);
            }

            // If the event was a 'click', draw the pressed state first and then release for
            // next frame.
            if (mouseJustPressed && mouseJustReleased) {

                calculateHovered();
                calculatePressed();
            }

            mouseJustPressed = false;
            mouseJustReleased = false;

            return render;
        }
    }

    public final void interact() {
    }

    public Player(long playerId, EntityInstance entity) {

        this.playerId = playerId;
        this.entity = entity;
    }

    @Override
    public final int hashCode() {

        return Long.hashCode(playerId);
    }

    @Override
    public final boolean equals(Object obj) {

        if (obj == null || !(obj instanceof Player)) {

            return false;
        }

        return obj == this;
    }
}
