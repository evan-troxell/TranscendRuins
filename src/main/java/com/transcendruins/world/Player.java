package com.transcendruins.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.entities.EntityInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceContext;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.assets.interfaces.UIComponent;
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

    public final void setLocation(String location) {

        this.location = location;
    }

    public final String getLocation() {

        return location;
    }

    private boolean uiUpdated = true;

    private boolean forceRender = false;

    private BufferedImage render;

    private final ArrayList<InterfaceInstance> uiPanels = new ArrayList<>();

    public final void setPanels(List<AssetPresets> interfacePresets) {

        replacePanels(interfacePresets.stream()
                .map(presets -> new InterfaceContext(presets, entity.getWorld(), entity, playerId, null)).toList());

    }

    public final void displayInventory(InventoryInstance secondaryInventory, InventoryComponentSchema secondaryUi) {

        InventoryInstance primaryInventory = entity.getInventory();
        InventoryComponentSchema primaryUi = entity.getInventoryUi();

        if (primaryUi == null || secondaryUi == null) {

            return;
        }

        InterfaceContext context = InterfaceContext.createInventoryDisplayContext(entity.getWorld(), entity, playerId,
                primaryInventory, primaryUi, secondaryInventory, secondaryUi);
        replacePanels(List.of(context));
    }

    private void replacePanels(List<InterfaceContext> contexts) {

        pressFocus = null;

        uiPanels.clear();
        contexts.forEach(context -> uiPanels.add((InterfaceInstance) context.instantiate()));

        hovered.clear();
        pressed.clear();

        calculateSize();
        calculateHovered();
        if (mousePressed) {

            calculatePressed();
        }

        uiUpdated = true;
    }

    public final void updateUiPanels(double time) {

        for (InterfaceInstance panel : uiPanels) {

            panel.update(time);
        }

        calculateSize();
        calculateHovered();
        if (mousePressed) {

            calculatePressed();
        }

        uiUpdated = true;

    }

    private int screenWidth = 1;

    private int screenHeight = 1;

    public final void setScreenSize(int width, int height) {

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

        uiUpdated = true;

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

        if (mouseX == x && mouseY == y) {

            return;
        }

        mouseX = x;
        mouseY = y;

        calculateHovered();
        if (mousePressed) {

            calculatePressed();
        }

        uiUpdated = true;

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

        uiUpdated = true;

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
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            System.out.println("TEST");

            if (!panel.scroll(mouseX, mouseY, displacement, scrolled, timestamp)) {

                resizePanel(panel);
                break;
            }

            resizePanel(panel);
        }

        calculateHovered();
        calculatePressed();

        uiUpdated = true;

    }

    public final BufferedImage renderUi() {

        // if (!uiUpdated && !forceRender) {

        // return render;
        // }

        uiUpdated = false;
        forceRender = false;

        render = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
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
            forceRender = true;
        }

        mouseJustPressed = false;
        mouseJustReleased = false;

        return render;
    }

    public final void interact() {
    }

    public Player(long playerId, EntityInstance entity) {

        this.playerId = playerId;
        this.entity = entity;
    }
}
