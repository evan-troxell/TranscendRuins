/* Copyright 2026 Evan Troxell
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

package com.transcendruins.world;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceContext;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.assets.interfaces.InterfaceInstance.GlobalMapComponentInstance.LocationDisplay;
import com.transcendruins.assets.interfaces.UIComponent;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.calls.InteractionCall;

/**
 * <code>Player</code>: A class representing the runtime context of a player,
 * including their in-world entity, UI menus, and current state.
 */
public final class Player {

    /**
     * <code>long</code>: The ID of this <code>Player</code> instance.
     */
    private final long randomId;

    /**
     * <code>DeterministicRandom</code>: The random number generator of this
     * <code>Player</code> instance.
     */
    private final DeterministicRandom random;

    /**
     * Retrieves the random number generator of this <code>Player</code> instance.
     * 
     * @return <code>DeterministicRandom</code>: The <code>random</code> field of
     *         this <code>Player</code> instance.
     */
    public final DeterministicRandom getRandom() {

        return random;
    }

    /**
     * <code>EntityInstance</code>: The entity of this <code>Player</code> instance.
     */
    private final EntityInstance entity;

    /**
     * Retrieves the entity of this <code>Player</code> instance.
     * 
     * @return <code>EntityInstance</code>: The <code>entity</code> field of this
     *         <code>Player</code> instance.
     */
    public final EntityInstance getEntity() {

        return entity;
    }

    /**
     * <code>Object</code>: The synchronization lock used when modifying location
     * fields.
     */
    private final Object LOCATION_LOCK = new Object();

    /**
     * <code>double</code>: The X coordinate on the global map of this
     * <code>Player</code> instance.
     */
    private double globalMapX;

    /**
     * <code>double</code>: The Y coordinate on the global map of this
     * <code>Player</code> instance.
     */
    private double globalMapY;

    /**
     * Sets the current global map coordinates of this <code>Player</code> instance.
     * 
     * @param coordinates <code>Point2D</code>: The position to set.
     */
    public final void setGlobalMapCoordinates(Point2D coordinates) {

        synchronized (LOCATION_LOCK) {

            this.globalMapX = coordinates.getX();
            this.globalMapY = coordinates.getY();
        }
    }

    /**
     * Retrieves the global map coordinates of this <code>Player</code> instance.
     * 
     * @return <code>Point2D</code>: The <code>globalMapX</code> and
     *         <code>globalMapY</code> fields of this <code>Player</code> instance.
     */
    public final Point2D getGlobalMapCoordinates() {

        synchronized (LOCATION_LOCK) {

            return new Point2D.Double(globalMapX, globalMapY);
        }
    }

    /**
     * <code>String</code>: The current location of this <code>Player</code>
     * instance.
     */
    private String location;

    /**
     * Sets the current location of this <code>Player</code> instance.
     * 
     * @param location <code>String</code>: The <code>location</code> field of this
     *                 <code>Player</code> instance.
     */
    public final void setLocation(String location) {

        synchronized (LOCATION_LOCK) {

            this.location = location;
        }
    }

    /**
     * Retrieves the current location of this <code>Player</code> instance.
     * 
     * @return <code>String</code>: The <code>location</code> field of this
     *         <code>Player</code> instance.
     */
    public final String getLocation() {

        synchronized (LOCATION_LOCK) {

            return location;
        }
    }

    /**
     * <code>Object</code>: The synchronization lock used when modifying UI fields.
     */
    private final Object UI_LOCK = new Object();

    /**
     * <code>ArrayList&lt;InterfaceInstance&gt;</code>: The current UI panels of
     * this <code>Player</code> instance.
     */
    private final ArrayList<InterfaceInstance> uiPanels = new ArrayList<>();

    /**
     * Sets the current UI panels of this <code>Player</code> instance.
     * 
     * @param interfacePresets <code>List&lt;AssetPresets&gt;</code>: The presets of
     *                         the interfaces to apply.
     */
    public final void setPanels(List<AssetPresets> interfacePresets) {

        replacePanels(interfacePresets.stream()
                .map(presets -> new InterfaceContext(presets, entity.getWorld(), entity, randomId, null)).toList());

    }

    /**
     * <code>boolean</code>: Whether or not this <code>Player</code> instance is on
     * the global map.
     */
    private boolean onGlobalMap;

    /**
     * Retrieves whether or not this <code>Player</code> instance is on the global
     * map.
     * 
     * @return <code>boolean</code>: The <code>onGlobalMap</code> field of this
     *         <code>Player</code> instance.
     */
    public final boolean onGlobalMap() {

        synchronized (UI_LOCK) {

            return onGlobalMap;
        }
    }

    /**
     * Exits the current location and enters the global map.
     */
    public final void enterGlobalMap() {

        synchronized (UI_LOCK) {

            if (onGlobalMap) {

                return;
            }

            onGlobalMap = true;
            InterfaceContext globalMapContext = InterfaceContext.createGlobalMapContext(entity.getWorld(), entity,
                    randomId, globalMapX, globalMapY);
            uiPanels.addFirst((InterfaceInstance) globalMapContext.instantiate());

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    /**
     * Exits the global map and enters the current location.
     */
    public final void exitGlobalMap() {

        synchronized (UI_LOCK) {

            if (!onGlobalMap) {

                return;
            }

            onGlobalMap = false;
            uiPanels.removeFirst();

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    /**
     * Displays the global map information of a specific location.
     * 
     * @param locationDisplay <code>LocationDisplay</code>: The global map
     *                        information to display.
     */
    public final void displayLocation(LocationDisplay locationDisplay) {

        InterfaceContext context = InterfaceContext.createLocationDisplayContext(entity.getWorld(), entity, randomId,
                locationDisplay);
        replacePanels(List.of(context));
    }

    /**
     * Displays 2 parallel inventories.
     * 
     * @param other <code>PrimaryAssetInstance</code>: The other asset whose
     *              inventory to display.
     */
    public final void displayInventory(PrimaryAssetInstance other) {

        InventoryInstance secondaryInventory = other.getInventory();
        InventoryComponentSchema secondaryUi = other.getInventoryUi();

        InventoryInstance primaryInventory = entity.getInventory();
        InventoryComponentSchema primaryUi = entity.getPrivateInventoryUi();

        if (primaryUi == null || secondaryUi == null) {

            return;
        }

        InterfaceContext context = InterfaceContext.createInventoryDisplayContext(entity.getWorld(), entity, randomId,
                primaryInventory, primaryUi, secondaryInventory, secondaryUi);
        replacePanels(List.of(context));
    }

    /**
     * Replaces the current UI panels with another set.
     * 
     * @param contexts <code>List&lt;InterfaceContext&gt;</code>: The interface
     *                 contexts to apply.
     */
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

            long time = System.currentTimeMillis();
            pointerCapture.forEach(layout -> {

                Point relativeMousePosition = getCumulativeComponentOffset(layout);

                layout.onExit(relativeMousePosition.x, relativeMousePosition.y);
                layout.onRelease(relativeMousePosition.x, relativeMousePosition.y);
                layout.onTriggerRelease(relativeMousePosition.x, relativeMousePosition.y, layout.getValue(), time);
            });
            pointerCapture.clear();

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    /**
     * Updates this <code>Player</code> instance.
     * 
     * @param time <code>double</code>: The current time in seconds.
     */
    public final void update(double time) {

        synchronized (UI_LOCK) {

            for (InterfaceInstance panel : uiPanels) {

                panel.update(time);
            }

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }

        updateInteract(time);
    }

    /**
     * <code>int</code>: The width of the screen.
     */
    private int screenWidth = 1;

    /**
     * <code>int</code>: The height of the screen.
     */
    private int screenHeight = 1;

    /**
     * Retrieves the size of the screen.
     * 
     * @return <code>Dimension</code>: The <code>screenWidth</code> and
     *         <code>screenHeight</code> fields of this <code>Player</code>
     *         instance.
     */
    public final Dimension getScreenSize() {

        return new Dimension(screenWidth, screenHeight);
    }

    /**
     * Sets the size of the screen.
     * 
     * @param width  <code>int</code>: The width of the screen.
     * @param height <code>int</code>: The height of the screen.
     */
    public final void setScreenSize(int width, int height) {

        synchronized (UI_LOCK) {

            if (screenWidth == width && screenHeight == height) {

                return;
            }

            screenWidth = width;
            screenHeight = height;

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    /**
     * Updates the sizing of all UI panels.
     */
    private void updateUiSize() {

        for (InterfaceInstance panel : uiPanels) {

            resizePanel(panel);
        }
    }

    /**
     * Resizes a UI panel.
     * 
     * @param panel <code>InterfaceInstance</code>: The panel to resize.
     */
    private void resizePanel(InterfaceInstance panel) {

        panel.renderBounds(screenWidth, screenHeight, 16, Style.EMPTY);
        panel.rescale(screenWidth, screenHeight);
    }

    /**
     * <code>int</code>: The current X coordinate of the mouse.
     */
    private int mouseX = -1;

    /**
     * <code>int</code>: The current Y coordinate of the mouse.
     */
    private int mouseY = -1;

    /**
     * <code>ArrayList&lt;UIComponent&gt;</code>: The UI components that are
     * currently hovered.
     */
    private final ArrayList<UIComponent> hovered = new ArrayList<>();

    /**
     * Sets the current position of the mouse.
     * 
     * @param x <code>int</code>: The current X coordinate of the mouse.
     * @param y <code>int</code>: The current Y coordinate of the mouse.
     */
    public final void setMousePosition(int x, int y) {

        synchronized (UI_LOCK) {

            if (mouseX == x && mouseY == y) {

                return;
            }

            mouseX = x;
            mouseY = y;

            updateUiSize();
            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    /**
     * Updates the hover status of all UI panels.
     */
    private void updateUiHovered() {

        long time = System.currentTimeMillis();

        ArrayList<UIComponent> newHovered = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            if (!panel.hover(mouseX, mouseY, newHovered, time)) {

                break;
            }
        }

        hovered.removeAll(pointerCapture);
        pointerCapture.forEach(layout -> {

            Point relativeMousePosition = getCumulativeComponentOffset(layout);
            layout.onHover(relativeMousePosition.x, relativeMousePosition.y);
        });

        hovered.removeAll(newHovered);
        exitAll();
        hovered.addAll(newHovered);
    }

    /**
     * Exits the mouse from all UI components and clears all UI component hovers.
     */
    private void exitAll() {

        for (UIComponent exited : hovered) {

            exited.onExit(mouseX, mouseY);
        }

        hovered.clear();
    }

    /**
     * <code>boolean</code>: Whether or not the mouse is currently pressed.
     */
    private boolean mousePressed;

    /**
     * <code>boolean</code>: Whether or not there have been no frames since the
     * mouse was pressed.
     */
    private boolean mouseJustPressed;

    /**
     * <code>boolean</code>: Whether or not there have been no frames since the
     * mouse was released.
     */
    private boolean mouseJustReleased;

    /**
     * <code>ArrayList&lt;UIComponent&gt; The UI components that are currently
     * pressed.
     */
    private final ArrayList<UIComponent> pressed = new ArrayList<>();

    private final ArrayList<UIComponent> pointerCapture = new ArrayList<>();

    /**
     * Sets the current current mouse press state.
     * 
     * @param pressed <code>boolean</code>: Whether or not the mouse is pressed.
     */
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

                updateUiPressed();
                updateUiTriggerPress();

                pointerCapture.addAll(this.pressed.stream().filter(UIComponent::getPointerCapture).toList());
            } else {

                releaseAll();
                UIComponent triggerConsumer = updateUiTriggerRelease();

                long time = System.currentTimeMillis();
                pointerCapture.forEach(layout -> {

                    Point relativeMousePosition = getCumulativeComponentOffset(layout);

                    layout.onExit(relativeMousePosition.x, relativeMousePosition.y);
                    layout.onRelease(relativeMousePosition.x, relativeMousePosition.y);

                    if (layout != triggerConsumer) {
                        layout.onTriggerRelease(relativeMousePosition.x, relativeMousePosition.y, layout.getValue(),
                                time);
                    }
                });
                pointerCapture.clear();
            }
        }
    }

    /**
     * Updates the press status of all UI panels.
     */
    private void updateUiPressed() {

        long time = System.currentTimeMillis();

        ArrayList<UIComponent> newPressed = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            if (!panel.press(mouseX, mouseY, newPressed, time)) {

                break;
            }
        }

        pressed.removeAll(pointerCapture);
        pointerCapture.forEach(layout -> {

            Point relativeMousePosition = getCumulativeComponentOffset(layout);
            layout.onPress(relativeMousePosition.x, relativeMousePosition.y);
        });

        pressed.removeAll(newPressed);
        releaseAll();
        pressed.addAll(newPressed);
    }

    /**
     * Releases the mouse and clears all UI component presses.
     */
    private void releaseAll() {

        for (UIComponent exited : pressed) {

            exited.onRelease(mouseX, mouseY);
        }

        pressed.clear();
    }

    /**
     * <code>UIComponent</code>: The current mouse press focus.
     */
    private UIComponent pressFocus = null;

    /**
     * Triggers the actions of all UI components that are pressed.
     */
    private void updateUiTriggerPress() {

        pressFocus = null;
        long time = System.currentTimeMillis();

        ArrayList<UIComponent> clicked = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            pressFocus = panel.triggerPress(mouseX, mouseY, clicked, time);

            if (pressFocus != null) {

                break;
            }
        }
    }

    /**
     * Triggers the actions of all UI components that are released.
     */
    private UIComponent updateUiTriggerRelease() {

        long time = System.currentTimeMillis();

        ArrayList<UIComponent> clicked = new ArrayList<>();
        for (int i = uiPanels.size() - 1; i >= 0; i--) {

            InterfaceInstance panel = uiPanels.get(i);
            UIComponent consumer = panel.triggerRelease(mouseX, mouseY, clicked, time);
            if (consumer != null) {

                return consumer;
            }
        }

        return null;
    }

    /**
     * Scrolls the mouse.
     * 
     * @param dx <code>int</code>: The horizontal distance the mouse was scrolled in
     *           pixels.
     * @param dy <code>int</code>: The vertical distance the mouse was scrolled in
     *           pixels.
     */
    public final void mouseScroll(int dx, int dy) {

        if (dx == 0 && dy == 0) {

            return;
        }

        long time = System.currentTimeMillis();
        Point displacement = new Point(dx, dy);

        ArrayList<UIComponent> scrolled = new ArrayList<>();

        synchronized (UI_LOCK) {
            for (int i = uiPanels.size() - 1; i >= 0; i--) {

                InterfaceInstance panel = uiPanels.get(i);

                if (!panel.scroll(mouseX, mouseY, displacement, scrolled, time)) {

                    resizePanel(panel);
                    break;
                }

                resizePanel(panel);
            }

            updateUiHovered();
            if (mousePressed) {

                updateUiPressed();
            }
        }
    }

    private Point getCumulativeComponentOffset(UIComponent component) {

        int relativeMouseX = mouseX;
        int relativeMouseY = mouseY;

        while (component != null) {

            relativeMouseX -= component.getContentOffsetX();
            relativeMouseY -= component.getContentOffsetY();

            component = component.getParent();
        }

        return new Point(relativeMouseX, relativeMouseY);
    }

    /**
     * Renders the current state of the UI.
     * 
     * @return <code>BufferedImage</code>: The outputs from all active interfaces
     *         layered into a single image.
     */
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

                updateUiHovered();
                updateUiPressed();
            }

            mouseJustPressed = false;
            mouseJustReleased = false;

            return render;
        }
    }

    /**
     * <code>double</code>: The maximum interaction detection range of this
     * <code>Player</code> instance.
     */
    private final double interactionDetectionRange = 4.5;

    /**
     * Retrieves the maximum interaction interaction range of this
     * <code>Player</code> instance.
     * 
     * @return <code>double</code>: The <code>interactionDetectionRange</code> field
     *         of this <code>Player</code> instance.
     */
    public final double getInteractionDetectionRange() {

        return interactionDetectionRange;
    }

    /**
     * <code>double</code>: The maximum interaction range of this
     * <code>Player</code> instance.
     */
    private final double interactionRange = 2.5;

    /**
     * <code>InteractionCall</code>: The current interaction context of this
     * <code>Player</code> instance.
     */
    private InteractionCall interactCall;

    /**
     * Sets the current interaction context.
     * 
     * @param interactCall <code>InteractionCall</code>: The interaction context to
     *                     apply.
     */
    public final void setInteraction(InteractionCall interactCall) {

        this.interactCall = interactCall;
    }

    private boolean interactLocked = false;

    public final boolean getInteractLocked() {

        return interactLocked;
    }

    private double interactStart = -1;

    private boolean interacted = false;

    /**
     * Interacts with the current interaction target of this <code>Player</code>
     * instance.
     * 
     * @return <code>boolean</code>: Whehter or not the an interaction is active at
     *         the end of this call.
     */
    public final boolean interact() {

        if (interactCall != null) {

            System.out.println("INTERACT LOCKED");
            interactLocked = true;

            entity.stopAttack();

            return true;
        }

        return false;
    }

    private void startInteract(double time) {

        System.out.println("INTERACT");

        interactStart = time;
        interacted = false;

        // TODO play interact animation
    }

    private void stopInteract() {

        System.out.println("INTERACT ENDED");

        interactLocked = false;
        interactStart = -1;
        // TODO stop attack animation
    }

    private void updateInteract(double time) {

        // If the interact is not locked or the interact can't be performed, stop the
        // interact sequence.
        if (!interactLocked || interactCall == null || !interactCall.isValid()) {

            interactLocked = false;
            interactStart = -1;
            return;
        }

        AssetInteractionInstance interactInstance = interactCall.interaction();

        // If the entity is still tracking the target, check if the entity reached the
        // interact range.
        if (interactStart == -1) {

            double range = interactionRange * World.UNIT_TILE;

            Vector3f displacement = entity.getPosition().subtract(interactCall.target().getPosition());
            double r_sqr = displacement.dot(displacement);

            if (r_sqr <= range * range) {

                startInteract(time);
            }

            return;
        }

        System.out.println("INTERACT TIME: " + (time - interactStart));

        double duration = interactInstance.getDuration();
        double cooldown = interactInstance.getCooldown();

        // If the attack has not been inflicted yet, check if the duration has
        // concluded.
        if (!interacted && interactStart + duration <= time) {

            System.out.println("INTERACT FINISHED");

            interactInstance.call(interactCall.target(), time, this);
            interacted = true;
        }

        // Check if the duration and end cooldown has concluded to end the attack.
        if (interactStart + (duration + cooldown) <= time) {

            System.out.println("INTERACT RESET");
            interactStart = -1;
            interactLocked = false;
        }
    }

    /**
     * Sets whether or not to attack the current target of this <code>Player</code>
     * instance.
     * 
     * @param attack <code>boolean</code>: Whether or not this <code>Player</code>
     *               instance should attack.
     */
    public final void attack(boolean attack) {

        if (entity.attack(attack)) {

            stopInteract();
        }
    }

    /**
     * Creates a new instance of the <code>Player</code> class.
     * 
     * @param randomId <code>long</code>: The ID of this <code>Player</code>
     *                 instance.
     * @param entity   <code>EntityInstance</code>: The entity of this
     *                 <code>Player</code> instance.
     */
    public Player(long randomId, EntityInstance entity) {

        this.randomId = randomId;
        random = new DeterministicRandom(randomId);

        this.entity = entity;
    }

    @Override
    public final int hashCode() {

        return Long.hashCode(randomId);
    }

    @Override
    public final boolean equals(Object obj) {

        if (obj == null || !(obj instanceof Player)) {

            return false;
        }

        return obj == this;
    }
}
