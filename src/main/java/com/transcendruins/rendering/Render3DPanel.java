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

package com.transcendruins.rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.graphics3d.Camera3D;
import com.transcendruins.graphics3d.PolyGroup;
import com.transcendruins.graphics3d.RenderedPixel;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.RenderTriangle;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.ui.GraphicsPanel;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>Render3DPanel</code>: A class representing the game display object of
 * the program.
 */
public abstract non-sealed class Render3DPanel extends GraphicsPanel
        implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * <code>Matrix</code>: A matrix transform used to standardize the X, Y, and Z
     * coordinates. Current status: X axis: regular, Y axis: inverted, Z axis:
     * regular.
     */
    public static final Matrix DISPLAY_TRANSFORM = new Matrix(3, 3, new double[] {

            1, 0, 0, 0, -1, 0, 0, 0, -1 });

    /**
     * <code>boolean</code>: Whether or not this <code>Render3DPanel</code> instance
     * is currently active.
     */
    private boolean active = false;

    /**
     * <code>long</code>: The next time (in milliseconds) when the FPS counter will
     * be assigned to the <code>fps</code> field of this <code>Render3DPanel</code>
     * instance.
     */
    private long nextTime = 0;

    /**
     * <code>int</code>: The number of elapsed frames since the <code>fps</code>
     * field of this <code>Render3DPanel</code> instance was last assigned.
     */
    private int framesCounter = 0;

    /**
     * <code>int</code>: The current frames-per-second counter of this
     * <code>Render3DPanel</code> instance.
     */
    private int fps = 0;

    /**
     * Retrieves the FPS counter of this <code>Render3DPanel</code> instance.
     * 
     * @return <code>int</code>: The <code>fps</code> field of this
     *         <code>Render3DPanel</code> instance.
     */
    public final int getFPS() {

        return fps;
    }

    /**
     * <code>Object</code>: The synchronized lock used to ensure all access to ths
     * <code>renderInstance</code> field of this <code>Render3DPanel</code> instance
     * is synchronized for thread safety.
     */
    private final Object renderLock = new Object();

    private RenderedPixel[] pixelDepths;

    private ImageIcon renderImage;

    /**
     * Creates a new instance of the <code>Render3DPanel</code> class.
     * 
     * @param name <code>String</code>: The name of this <code>Render3DPanel</code>
     *             instance.
     */
    public Render3DPanel(String name) {

        super(name, ComponentSettings.BACKGROUND_PANEL_SETTINGS);
        initializeInputListeners();
    }

    /**
     * Initializes the input listeners of this <code>Render3DPanel</code> instance.
     */
    private void initializeInputListeners() {

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    /**
     * Paints this <code>Render3DPanel</code> instance. The body of this method is
     * synchronized, meaning it can be safely called without the possibility of the
     * <code>renderInstance</code> being altered during rendering.
     * 
     * @param g <code>Graphics</code>: The graphics object used to paint this
     *          <code>Render3DPanel</code> instance.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (active && renderImage != null) {

            Graphics2D g2D = (Graphics2D) g;
            synchronized (renderLock) {

                drawImage(0, 0, renderImage, 0, g2D);
            }
        }
    }

    /**
     * Renders a polygon group onto the <code>pixelDepths</code> array.
     * 
     * @param polygonGroup <code>PolyGroup</code>: The polygon group to render.
     * @param pixelDepths  <code>RenderedPixel[frameWidth * frameHeight]</code>: An
     *                     array of the mapped pixel distances to add onto.
     * @param frameWidth   <code>int</code>: The width of the frame to center on.
     * @param frameHeight  <code>int</code>: The height of the frame to center on.
     */
    public final void renderPolygonGroup(Collection<RenderTriangle> polygons, RenderInstance render) {

        RenderMaterialInstance renderMaterial = render.getRenderMaterial();

        int width = getWidth();
        int height = getHeight();

        // Render all polygons in the polygon group.
        for (RenderTriangle polygon : polygons) {

            // If the triangle is facing the wrong side, move on.
            if (renderMaterial.backfaceCulling() && polygon.facingBackside()) {

                continue;
            }

            int minX = Math.max(0, (int) polygon.getMinX());
            int maxX = Math.min(width - 1, (int) polygon.getMaxX());

            int minY = Math.max(0, (int) polygon.getMinY());
            int maxY = Math.min(height - 1, (int) polygon.getMaxY());
            // If the triangle is outside of the bounds of the screen, move on.
            if (minX > maxX || minY > maxY) {

                continue;
            }

            double fresnelFactor = Math.pow(1 - Math.pow(polygon.getViewCosine(), 2), 8);
            double opaqueFactor = renderMaterial.opaque() ? 1 : (renderMaterial.fresnelEffect() ? fresnelFactor : 0);

            // Applies face dimming to the polygon.
            double faceDimmingFactor = 1 - renderMaterial.faceDimmingFactor() * fresnelFactor;

            double area = polygon.getArea();

            Vector v1 = polygon.getVertex1();
            Vector v2 = polygon.getVertex2();
            Vector v3 = polygon.getVertex3();

            // Iterate through the box with the bounds of the minimum and maximum X and Y
            // values previously layed out.
            for (int x = minX; x <= maxX; x++) {

                // Render each pixel at point X from the minimum to the maximum Y values.
                for (int y = minY; y <= maxY; y++) {

                    Vector pixel = new Vector(x, y, 0);
                    double a1 = Triangle.calculateArea(pixel, v1, v2);
                    double a2 = Triangle.calculateArea(pixel, v2, v3);
                    double a3 = Triangle.calculateArea(pixel, v3, v1);
                    double a = a1 + a2 + a3;

                    // If the pixel is not inside the triangle, move on.
                    if (a < area - 0.0001 || area + 0.0001 < a) {

                        continue;
                    }

                    int depthsIndex = x + y * width;
                    double pixelDepth = polygon.depthAtPoint(x, y);

                    double[] uvCoords = polygon.getUvCoordinates(x, y);
                    Color rawColor = render.getRGB(uvCoords[0], uvCoords[1]);

                    int alpha = (int) (rawColor.getAlpha() * (1 - opaqueFactor) + 255 * opaqueFactor);
                    int red = (int) (rawColor.getRed() * faceDimmingFactor);
                    int green = (int) (rawColor.getGreen() * faceDimmingFactor);
                    int blue = (int) (rawColor.getBlue() * faceDimmingFactor);

                    if (renderMaterial.antiAliasing()) {

                        double alphaMultiplier = 1.0;
                        alpha *= alphaMultiplier;
                    }

                    int color = (alpha << 24) | (red << 16) | (green << 8) | blue;

                    if (pixelDepths[depthsIndex] != null) {

                        pixelDepths[depthsIndex].addDepth(pixelDepth, color);
                    } else {

                        pixelDepths[depthsIndex] = new RenderedPixel(x, y, pixelDepth, color);
                    }
                }
            }
        }
    }

    /**
     * Begins displaying this <code>Render3DPanel</code> instance.
     * 
     * @param models <code>Collection&lt;RenderInstance&gt;</code>: The models to be
     *               rendered.
     * @param camera <code>Camera3D</code>: The camera to render from.
     */
    public final void render(Collection<RenderInstance> models, Camera3D camera) {

        if (System.currentTimeMillis() > nextTime) {

            nextTime = System.currentTimeMillis() + 1000;
            fps = framesCounter;
            outputFPS(fps);
            framesCounter = 0;
        }
        framesCounter++;
        active = true;

        synchronized (renderLock) {

            ArrayList<PolyGroup> polygonGroups = new ArrayList<>();
            for (RenderInstance render : models) {

                polygonGroups.addAll(render.getPolygons());
            }

            int width = getWidth();
            int height = getHeight();

            // Generate the image which the rendering will be drawn onto.
            BufferedImage render = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D renderG2D = render.createGraphics();
            renderG2D.setColor(Color.BLACK);
            renderG2D.fillRect(0, 0, width, height);
            renderG2D.dispose();

            // An array of the distances of the pixels in the 'render' variable. Pixels will
            // begin with a null depth, and will be assigned automatically once drawn over.
            pixelDepths = new RenderedPixel[width * height];

            // Creates the thread pool used to manage threading polygon groups.
            ExecutorService threadPool = Executors.newCachedThreadPool();

            // Render all polygon groups.
            for (PolyGroup polygonGroup : polygonGroups) {

                Collection<RenderTriangle> polygons = polygonGroup.getPolygons(camera, width, height);
                threadPool.submit(new RenderTask(polygons, polygonGroup.getRender()));
            }

            try {

                // Await the termination of the thread pool. If it takes too long, forcefully
                // shut down the thread.
                threadPool.shutdown();
                if (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {

                    threadPool.shutdownNow(); // Force shutdown if not finished.
                }
            } catch (InterruptedException e) {

                threadPool.shutdownNow(); // Re-interrupt if necessary.
                Thread.currentThread().interrupt();
            }

            int backgroundColor = 0;

            // Assign all pixel colors in the 'pixelDepths' array to the 'render' variable.
            for (RenderedPixel pixel : pixelDepths) {

                if (pixel != null) {

                    render.setRGB(pixel.getX(), pixel.getY(), pixel.getColor(backgroundColor));
                }
            }

            renderImage = new ImageIcon(render);
        }

        repaint();
    }

    /**
     * Stops displaying this <code>Render3DPanel</code> instance.
     */
    public final void stop() {

        active = false;
    }

    /**
     * Outputs the FPS counter of this <code>Render3DPanel</code> instance.
     * 
     * @param outputFPS <code>int</code>: The outputted FPS of this
     *                  <code>Render3DPanel</code> instance.
     */
    public abstract void outputFPS(int outputFPS);

    /**
     * <code>Render3DPanel.RenderTask</code>: A class representing a thread which
     * can be sent polygon lists to render, used to assist in rendering a main
     * image.
     */
    private final class RenderTask implements Callable<Void> {

        /**
         * <code>Collection&lt;RenderTriangle&gt;</code>: The polygons to render.
         */
        private final Collection<RenderTriangle> polygons;

        /**
         * <code>RenderInstance</code>: The context to render using
         */
        private final RenderInstance render;

        /**
         * Creates a new instance of the <code>Render3DPanel.RenderTask</code> class.
         * 
         * @param polygons <code>Collection&lt;RenderTriangle&gt;</code>: The polygons
         *                 to render.
         * @param render   <code>RenderInstance</code>: The context to render using.
         */
        public RenderTask(Collection<RenderTriangle> polygons, RenderInstance render) {

            this.polygons = polygons;
            this.render = render;
        }

        /**
         * Renders this <code>RenderTask</code> instance.
         */
        @Override
        public Void call() {

            renderPolygonGroup(polygons, render);
            return null;
        }
    }
}
