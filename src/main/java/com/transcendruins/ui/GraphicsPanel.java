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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import com.transcendruins.ui.mappedcomponents.containers.TRLayeredPanel;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>GraphicsPanel</code>: A class representing on which an image may be
 * displayed.
 */
sealed public class GraphicsPanel extends TRLayeredPanel permits Render3D {

    /**
     * Creates a new instance of the <code>GraphicsPanel</code> class.
     * 
     * @param name     <code>String</code>: The name of this
     *                 <code>GraphicsPanel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>GraphicsPanel</code> instance.
     */
    public GraphicsPanel(String name, ComponentSettings settings) {

        super(name, settings);
    }

    /**
     * Draws an image on this <code>GraphicsPanel</code> instance. This method is
     * synchronized, meaning other invocations to this <code>GraphicsPanel</code>
     * instance can safely be made from other threads without interfering with this
     * method.
     * 
     * @param x      <code>int</code>: The x coordinate to display the image at.
     * @param y      <code>int</code>: The y coordinate to display the image at.
     * @param width  <code>int</code>: The width to stretch the image to.
     * @param height <code>int</code>: The height to stretch the image to.
     * @param icon   <code>ImageIcon</code>: The image to display.
     * @param angle  <code>double</code>: The angle of the image, specified in
     *               clockwise degrees.
     * @param g2D    <code>Graphics2D</code>: The graphics context to use.
     */
    public final synchronized void drawImage(int x, int y, int width, int height, ImageIcon icon, double angle,
            Graphics2D g2D) {

        angle *= -Math.PI / 180;
        AffineTransform backup = rotate(x + width, y + height, angle, g2D);
        g2D.drawImage(icon.getImage(), x, y, 2 * width, 2 * height, this);
        g2D.setTransform(backup);
    }

    /**
     * Draws an image on this <code>GraphicsPanel</code> instance. This method is
     * synchronized, meaning other invocations to this <code>GraphicsPanel</code>
     * instance can safely be made from other threads without interfering with this
     * method.
     * 
     * @param x     <code>int</code>: The x coordinate to display the image at.
     * @param y     <code>int</code>: The y coordinate to display the image at.
     * @param scale <code>double</code>: The scale to display the image at.
     * @param icon  <code>ImageIcon</code>: The image to display.
     * @param angle <code>double</code>: The angle of the image, specified in
     *              clockwise degrees.
     * @param g2D   <code>Graphics2D</code>: The graphics context to use.
     */
    public final synchronized void drawImage(int x, int y, double scale, ImageIcon icon, double angle, Graphics2D g2D) {

        drawImage(x, y, (int) (icon.getIconWidth() * scale), (int) (icon.getIconHeight() * scale), icon, angle, g2D);
    }

    /**
     * Draws an image on this <code>GraphicsPanel</code> instance. This method is
     * synchronized, meaning other invocations to this <code>GraphicsPanel</code>
     * instance can safely be made from other threads without interfering with this
     * method.
     * 
     * @param x     <code>int</code>: The x coordinate to display the image at.
     * @param y     <code>int</code>: The y coordinate to display the image at.
     * @param icon  <code>ImageIcon</code>: The image to display.
     * @param angle <code>float</code>: The angle of the image, specified in
     *              clockwise degrees.
     * @param g2D   <code>Graphics2D</code>: The graphics context to use.
     */
    public final synchronized void drawImage(int x, int y, ImageIcon icon, double angle, Graphics2D g2D) {

        drawImage(x, y, 1.0, icon, angle, g2D);
    }

    /**
     * Rotates the graphics context by a set angle around a center point.
     * 
     * @param x     <code>double</code>: The x coordinate to rotate about.
     * @param y     <code>double</code>: The y coordinate to rotate about.
     * @param angle <code>double</code>: The angle of the rotation, specified in
     *              counter-clockwise radians.
     * @param g2D   <code>Graphics2D</code>: The graphics context to rotate.
     * @return <code>AffineTransform</code>: The backup transformation object, used
     *         to return to the previous save.
     */
    private static AffineTransform rotate(double x, double y, double angle, Graphics2D g2D) {

        AffineTransform backup = g2D.getTransform();
        g2D.setTransform(AffineTransform.getRotateInstance(angle, x, y));
        return backup;
    }
}
