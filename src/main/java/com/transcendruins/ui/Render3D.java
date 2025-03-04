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

import com.transcendruins.assets.extra.RenderInstance;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.graphics3d.Camera3D;
import com.transcendruins.graphics3d.PolyGroup;
import com.transcendruins.graphics3d.RenderedPixel;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.RenderTriangle;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>Render3D</code>: A class representing the game display object of the
 * program.
 */
non-sealed public abstract class Render3D extends GraphicsPanel
        implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * <code>Matrix</code>: A matrix transform used to standardize the X, Y, and Z
     * coordinates.
     * Current status: X axis: regular, Y axis: inverted, Z axis: regular.
     */
    public static final Matrix DISPLAY_TRANSFORM = new Matrix(3, 3, new double[] {

            1, 0, 0,
            0, -1, 0,
            0, 0, -1
    });

    /**
     * <code>boolean</code>: Whether or not this <code>Render3D</code> instance is
     * currently active.
     */
    private boolean active = false;

    /**
     * <code>long</code>: The next time (in milliseconds) when the FPS counter will
     * be assigned to the <code>fps</code> field of this <code>Render3D</code>
     * instance.
     */
    private long nextTime = 0;

    /**
     * <code>int</code>: The number of elapsed frames since the <code>fps</code>
     * field of this <code>Render3D</code> instance was last assigned.
     */
    private int framesCounter = 0;

    /**
     * <code>int</code>: The current frames-per-second counter of this
     * <code>Render3D</code> instance.
     */
    private int fps = 0;

    /**
     * Retrieves the FPS counter of this <code>Render3D</code> instance.
     * 
     * @return <code>int</code>: The <code>fps</code> field of this
     *         <code>Render3D</code> instance.
     */
    public final int getFPS() {

        return fps;
    }

    /**
     * <code>Object</code>: The synchronized lock used to ensure all access to ths
     * <code>renderInstance</code> field of this <code>Render3D</code> instance is
     * synchronized for thread safety.
     */
    private final Object renderLock = new Object();

    private RenderedPixel[] pixelDepths;

    private ImageIcon renderImage;

    /**
     * Creates a new instance of the <code>Render3D</code> class.
     * 
     * @param name <code>String</code>: The name of this <code>Render3D</code>
     *             instance.
     */
    public Render3D(String name) {

        super(name, ComponentSettings.BACKGROUND_PANEL_SETTINGS);
        initializeInputListeners();
    }

    /**
     * Initializes the input listeners of this <code>Render3D</code> instance.
     */
    private void initializeInputListeners() {

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    /**
     * Paints this <code>Render3D</code> instance. The body of this method is
     * synchronized, meaning it can be safely called without the possibility of the
     * <code>renderInstance</code> being altered during rendering.
     * 
     * @param g <code>Graphics</code>: The graphics object used to paint this
     *          <code>Render3D</code> instance.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (active) {

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
     * @param pixelDepths  <code>RenderedPixel[frameWidth * frameHeight]</code>:
     *                     An array of the mapped pixel distances to add onto.
     * @param frameWidth   <code>int</code>: The width of the frame to center
     *                     on.
     * @param frameHeight  <code>int</code>: The height of the frame to center
     *                     on.
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

            double fresnelFactor = Math.pow(1 - Math.pow(polygon.getViewCosine(), 2), 8);
            double opaqueFactor = renderMaterial.opaque() ? 1 : (renderMaterial.fresnelEffect() ? fresnelFactor : 0);

            // Applies face dimming to the polygon.
            double faceDimmingFactor = 1 - renderMaterial.faceDimmingFactor() * fresnelFactor;

            // Iterate through the box with the bounds of the minimum and maximum X and Y
            // values previously layed out.
            for (int x = minX; x <= maxX; x++) {

                int[] yBounds = polygon.findYBoundsAtX(x);

                // Sets the Y bounds to iterate through.
                int minY = Math.max(yBounds[0], 0);
                int maxY = Math.min(yBounds[1], height);

                if (minY == maxY) {

                    continue;
                }

                // Render each pixel at point X from the minimum to the maximum Y values.
                for (int y = minY; y <= maxY; y++) {

                    int[] xBounds = polygon.findXBoundsAtY(y);
                    int minX2 = Math.max(xBounds[0], 0);
                    int maxX2 = Math.min(xBounds[1], width);

                    if (minX2 == maxX2) {

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

                        // If the pixel intercepts the top X bounds, adjust the alpha accordingly.
                        if (x == xBounds[0]) {

                            double pixelPct = 1.0;
                            alphaMultiplier *= pixelPct;
                        }

                        // If the pixel intercepts the bottom X bounds, adjust the alpha accordingly.
                        if (x == xBounds[1]) {

                            double pixelPct = 1.0;
                            alphaMultiplier *= pixelPct;
                        }

                        // If the pixel intercepts the bottom X bounds, adjust the alpha accordingly.
                        if (y == yBounds[0]) {

                            double pixelPct = 1.0;
                            alphaMultiplier *= pixelPct;
                        }

                        // If the pixel intercepts the bottom X bounds, adjust the alpha accordingly.
                        if (y == yBounds[1]) {

                            double pixelPct = 1.0;
                            alphaMultiplier *= pixelPct;
                        }

                        if (alphaMultiplier > 1) {

                            alphaMultiplier = 1;
                        }
                        if (alphaMultiplier < 0) {

                            alphaMultiplier = 0;
                        }

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
     * Begins displaying this <code>Render3D</code> instance.
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
     * Stops displaying this <code>Render3D</code> instance.
     */
    public final void stop() {

        active = false;
    }

    /**
     * Outputs the FPS counter of this <code>Render3D</code> instance.
     * 
     * @param outputFPS <code>int</code>: The outputted FPS of this
     *                  <code>Render3D</code> instance.
     */
    public abstract void outputFPS(int outputFPS);

    /**
     * <code>Render3D.RenderTask</code>: A class representing a thread which can be
     * sent
     * polygon lists to render, used to assist in rendering a main image.
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
         * Creates a new instance of the <code>Render3D.RenderTask</code> class.
         * 
         * @param polygons <code>Collection&lt;RenderTriangle&gt;</code>: The
         *                 polygons to
         *                 render.
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
