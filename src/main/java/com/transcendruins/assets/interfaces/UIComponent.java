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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style;

/**
 * <code>UIComponent</code>: An interface representing a UI component which can
 * be rendered on interface canvas.
 */
public interface UIComponent {

    public void unhover(int mouseX, int mouseY);

    public void hover(int mouseX, int mouseY);

    public default boolean hover(int mouseX, int mouseY, List<UIComponent> stack) {

        return propagateAction(mouseX, mouseY, (component, x, y) -> {

            component.hover(x, y);
            return true;
        }, stack);
    }

    public void scroll(int mouseX, int mouseY, Point displacement);

    public default boolean scroll(int mouseX, int mouseY, Point displacement, List<UIComponent> stack) {

        return propagateAction(mouseX, mouseY, (component, x, y) -> {

            component.scroll(x, y, displacement);
            return displacement.x != 0 || displacement.y != 0;
        }, stack);
    }

    public void release(int mouseX, int mouseY);

    public boolean press(int mouseX, int mouseY);

    public default boolean press(int mouseX, int mouseY, List<UIComponent> stack) {

        return propagateAction(mouseX, mouseY, UIComponent::press, stack);
    }

    /**
     * Evaluates the event which should be run when this <code>UIComponent</code>
     * instance is clicked.
     * 
     * @param mouseX <code>int</code>: The X coordinate which was clicked.
     * @param mouseY <code>int</code>: The Y coordinate which was clicked.
     * @param value  <code>TRScript</code>: The value of this
     *               <code>UIComponent</code> instance.
     * @return <code>boolean</code>: Whether or not the event should continue to
     *         propogate.
     */
    public boolean onClick(int mouseX, int mouseY, TRScript value);

    public default boolean click(int mouseX, int mouseY, List<UIComponent> stack) {

        return propagateAction(mouseX, mouseY, (component, x, y) -> component.onClick(x, y, component.getValue()),
                stack);
    }

    public List<UIComponent> getChildren();

    public TRScript getValue();

    /**
     * <code>PropagationAction</code>: The action to apply while propagating through
     * the UI component stack.
     */
    @FunctionalInterface
    public interface PropagationAction {

        /**
         * Applies a function while propagating through the UI stack.
         * 
         * @param component <code>UIComponent</code>: The component to apply the
         *                  propagation function to.
         * @param mouseX    <code>int</code>: The X coordinate of the event to
         *                  propagate.
         * @param mouseY    <code>int</code>: The Y coordinate of the event to
         *                  propagate.
         * @return <code>boolean</code>: Whether or not to continue propagating.
         */
        public boolean apply(UIComponent component, int mouseX, int mouseY);
    }

    /**
     * Propagates an action through the UI stack of this <code>UIComponent</code> at
     * a given coordinate.
     * 
     * @param mouseX <code>int</code>: The X coordinate of the event to propagate.
     * @param mouseY <code>int</code>: The Y coordinate of the event to propagate.
     * @param action <code>PropagationAction</code>: The action to propagate. This
     *               action will be evaluated from the topmost element downwards for
     *               as long as it returns <code>true</code>.
     * @param stack  <code>List&lt;UIComponent&gt;</code>: The stack of components
     *               which have been propagated through. This list begins at the
     *               topmost elements which were propagated through and works
     *               downwards.
     * @return <code>boolean</code>: Whether to continue propagating the action
     *         through the next elements.
     */
    private boolean propagateAction(int mouseX, int mouseY, PropagationAction action, List<UIComponent> stack) {

        List<UIComponent> children = getChildren();

        boolean propagate = true;

        // Start with the top children and work backwards.
        for (int i = children.size() - 1; i >= 0; i--) {

            UIComponent child = children.get(i);

            // If the point is in the child's bounds, operate on it.
            if (child.contains(mouseX, mouseY)) {

                // Adjust into the new reference frame.
                int adjustedX = mouseX - getX();
                int adjustedY = mouseY - getY();

                // If the child does not propagate, end propagation.
                if (!child.propagateAction(adjustedX, adjustedY, action, stack)) {

                    propagate = false;
                    break;
                }
            }
        }

        // If the event has propagated to the parent, apply its function and add the
        // event to the stack.
        if (propagate) {

            stack.add(this);
            return action.apply(this, mouseX, mouseY);
        }

        return false;
    }

    /**
     * Calculates whether or not this <code>UIComponent</code> instance contains a
     * given coordinate pair.
     * 
     * @param x <code>int</code>: The X coordinate to check.
     * @param y <code>int</code>: The Y coordinate to check.
     * @return <code>boolean</code>: Whether or not the coordinate is contained.
     */
    public default boolean contains(int x, int y) {

        int adjustedX = x - getX();
        int adjustedY = y - getY();

        Dimension contentSize = getSize();

        return 0 <= adjustedX && adjustedX < contentSize.width && 0 <= adjustedY && adjustedY < contentSize.height;
    }

    /**
     * Retrieves the X coordinate of this <code>UIComponent</code> instance.
     * 
     * @return <code>int</code>: The X coordinate of this <code>UIComponent</code>
     *         instance from a reference frame not centered on ths component.
     */
    public int getX();

    /**
     * Retrieves the Y coordinate of this <code>UIComponent</code> instance.
     * 
     * @return <code>int</code>: The Y coordinate of this <code>UIComponent</code>
     *         instance from a reference frame not centered on ths component.
     */
    public int getY();

    /**
     * Retrieves the size of this <code>UIComponent</code> instance. This
     * encompasses any border, padding, and content dimensions, though it should not
     * include margins (as they are not bounded by the component).
     * 
     * @return <code>Dimension</code>: The calculated dimensions.
     */
    public Dimension getSize();

    /**
     * Draws this <code>UIComponent</code> instance.
     * 
     * @param parentWidth    <code>int</code>: The content width of the parent of
     *                       this <code>UIComponent</code> instance.
     * @param parentHeight   <code>int</code>: The content height of the parent of
     *                       this <code>UIComponent</code> instance.
     * @param parentFontSize <code>int</code>: The font size of the parent of this
     *                       <code>UIComponent</code> instance.
     * @param parentStyle    <code>Style</code>: The raw style of the parent of this
     *                       <code>UIComponent</code> instance.
     */
    public BufferedImage render(int parentWidth, int parentHeight, int parentFontSize, Style parentStyle);
}
