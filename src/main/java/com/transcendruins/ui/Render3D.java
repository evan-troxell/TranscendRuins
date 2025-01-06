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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import com.transcendruins.geometry.Matrix;
import com.transcendruins.geometry.MatrixOperations;
import com.transcendruins.geometry.PolyGroup;
import com.transcendruins.geometry.Triangle3D;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.Camera3D;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.rendering.RenderTask;
import com.transcendruins.rendering.RenderedPixel;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;
import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialInstance;

/**
 * <code>Render3D</code>: A class representing the game display object of the program.
 */
public abstract class Render3D extends GraphicsPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * <code>Matrix</code>: A matrix transform used to standardize the X, Y, and Z coordinates.
     * Current status: X axis: regular, Y axis: inverted, Z axis: regular.
     */
    public static final Matrix DISPLAY_TRANSFORM = new Matrix(Vector.DIMENSION_3D, Vector.DIMENSION_3D, new double[] {

        1, 0, 0,
        0, -1, 0,
        0, 0, 1
    });

    /**
     * <code>Camera3D</code>: The camera to be rendered from.
     */
    public final Camera3D camera;

    /**
     * <code>ArrayList&lt;PolyGroup&gt;</code>: The polygon groups to be rendered.
     */
    private ArrayList<PolyGroup> polygonGroups = new ArrayList<>();

    /**
     * <code>boolean</code>: Whether or not this <code>Render3D</code> instance is currently active.
     */
    private boolean active = false;

    /**
     * <code>long</code>: The next time (in milliseconds) when the FPS counter will be assigned to the <code>fps</code> field of this <code>Render3D</code> instance.
     */
    private long nextTime;

    /**
     * <code>int</code>: The number of elapsed frames since the <code>fps</code> field of this <code>Render3D</code> instance was last assigned.
     */
    private int framesCounter = 0;

    /**
     * <code>int</code>: The current frames-per-second counter of this <code>Render3D</code> instance.
     */
    private int fps = -1;

    /**
     * <code>Object</code>: The synchronized lock used to ensure all access to ths <code>renderInstance</code> field of this <code>Render3D</code> instance is synchronized for thread safety.
     */
    private final Object renderLock = new Object();

    /**
     * Creates a new instance of the <code>Render3D</code> class.
     * @param name <code>String</code>: The name of this <code>Render3D</code> instance.
     * @param camera <code>Camera3D</code>: The camera to render from.
     */
    public Render3D(String name, Camera3D camera) {

        super(name, ComponentSettings.BACKGROUND_PANEL_SETTINGS);
        this.camera = camera;
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
     * Paints this <code>Render3D</code> instance. The body of this method is synchronized, meaning it can be safely called without the possibility of the <code>renderInstance</code> being altered during rendering.
     * @param g <code>Graphics</code>: The graphics object used to paint this <code>Render3D</code> instance.
     */
    @Override
    public void paintComponent(Graphics g) {

        synchronized (renderLock) {

            Graphics2D g2D = (Graphics2D) g;

            // Retrieves the matrix operations used to normalize the position of a polygon on frame.
            MatrixOperations normalize = camera.getRenderTransform();

            // Generate the image which the rendering will be drawn onto.
            BufferedImage render = new BufferedImage((int) (getWidth()), (int) (getHeight()), BufferedImage.TYPE_INT_RGB);
            Graphics2D renderG2D = render.createGraphics();
            renderG2D.setColor(Color.BLACK);
            renderG2D.fillRect(0, 0, render.getWidth(), render.getHeight());
            renderG2D.dispose();

            // An array of the distances of the pixels in the 'render' variable. Pixels will begin with a null depth, and will be assigned automatically once drawn over.
            RenderedPixel[] pixelDepths = new RenderedPixel[render.getWidth() * render.getHeight()];

            // Creates the thread pool used to manage threading polygon groups.
            ExecutorService threadPool = Executors.newCachedThreadPool();

            // Render all polygon groups.
            for (PolyGroup polygonGroup : polygonGroups) {

                threadPool.submit(new RenderTask(polygonGroup, normalize, pixelDepths, getWidth(), getHeight()));
            }

            try {

                // Await the termination of the thread pool. If it takes too long, forcefully shut down the thread.
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

                    render.setRGB(pixel.x, pixel.y, pixel.getColor(backgroundColor));
                }
            }

            render.setRGB(getWidth() / 2, getHeight() / 2, Color.WHITE.getRGB());
            drawImage(0, 0, new ImageIcon(render), 0, g2D);
        }

        super.paintComponent(g);

        if (System.currentTimeMillis() > nextTime) {

            nextTime = System.currentTimeMillis() + 1000;
            fps = framesCounter;
            outputFPS(fps);
            framesCounter = 0;
        }
        framesCounter++;

        if (active) {

            repaint();
        }
    }

    /**
     * Renders a polygon group onto the <code>pixelDepths</code> array.
     * @param polygonGroup <code>PolyGroup</code>: The polygon group to render.
     * @param verticeNormalizer <code>MatrixOperations</code>: The vertice normalizer used to adjust for the spacial position and orientation of the camera.
     * @param pixelDepths <code>RenderedPixel[frameWidth * frameHeight]</code>: An array of the mapped pixel distances to add onto.
     * @param frameWidth <code>int</code>: The width of the frame to center on.
     * @param frameHeight <code>int</code>: The height of the frame to center on.
     */
    public static final void renderPolygonGroup(PolyGroup polygonGroup, MatrixOperations verticeNormalizer, RenderedPixel[] pixelDepths, int frameWidth, int frameHeight) {

        // Render all polygons in the polygon group.
        for (Triangle3D polygon : polygonGroup.getPolygons()) {

            // Create the normalized triangle which will be displayed on the 'render' variable.
            Triangle3D normalizedTriangle = polygon.getAdjustedInstance(verticeNormalizer);

            RenderMaterialInstance renderMaterial = polygon.getRenderMaterial();

            // If the triangle is completely out of frame or facing the wrong side, move on.
            if (!normalizedTriangle.inFrame(frameWidth, frameHeight) || (renderMaterial.backfaceCulling() && normalizedTriangle.facingBackside())) {

                continue;
            }

            int minX = Math.max(0, normalizedTriangle.minX);
            int maxX = Math.min(frameWidth - 1, normalizedTriangle.maxX);

            double fresnelFactor = normalizedTriangle.viewCosine;
            double convertFactor = Math.pow(1 - fresnelFactor * fresnelFactor, 8);

            int red = polygon.color.getRed();
            int green = polygon.color.getGreen();
            int blue = polygon.color.getBlue();

            // Applies face dimming to the polygon.
            if (renderMaterial.faceDimming()) {

                double faceDimmingFactor = 1 - renderMaterial.faceDimmingFactor() * convertFactor;
                red *= faceDimmingFactor;
                green *= faceDimmingFactor;
                blue *= faceDimmingFactor;
            }

            int color = (red << 16) | (green << 8) | blue;

            int alpha = renderMaterial.opaque() ? 255 : polygon.color.getAlpha();

            // Applies the fresnel effect to the polygon.
            if (renderMaterial.fresnelEffect() && alpha < 255) {

                alpha += ((255 - alpha) * convertFactor);
            }

            // Iterate through the box with the bounds of the minimum and maximum X and Y values previously layed out.
            for (int x = minX; x <= maxX; x++) {

                int[] yBounds = normalizedTriangle.findYBoundsAtX(x);

                // Sets the Y bounds to iterate through.
                int minY = yBounds[0] < 0 ? 0 : yBounds[0];
                int maxY = yBounds[1] > frameHeight - 1 ? frameHeight - 1 : yBounds[1];

                if (minY == maxY) {

                    continue;
                }

                // Render each pixel at point X from the minimum to the maximum Y values.
                for (int y = minY; y <= maxY; y++) {

                    int[] xBounds = normalizedTriangle.findXBoundsAtY(y);

                    int depthsIndex = x + y * frameWidth;
                    double pixelDepth = normalizedTriangle.depthAtPoint(x, y);
                    int instanceAlpha = alpha;

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

                        instanceAlpha *= alphaMultiplier;
                    }

                    if (pixelDepths[depthsIndex] != null) {

                        pixelDepths[depthsIndex].addDepth(pixelDepth, (instanceAlpha << 24) | color);
                    } else {

                        pixelDepths[depthsIndex] = new RenderedPixel(x, y, pixelDepth, (instanceAlpha << 24) | color);
                    }
                }
            }
        }
    }

    /**
     * Parses the polygons in the stored render instances of this <code>Render3D</code> instance into polygon groups.
     * @param models <code>Collection&ltRenderInstance&gt;</code>: The models to be rendered.
     * @return <code>ArrayList&lt;PolyGroup&gt;</code>: The retrieved polygon groups.
     */
    private ArrayList<PolyGroup> getPolygonGroups(Collection<RenderInstance> models) {

        ArrayList<PolyGroup> polygons = new ArrayList<>();
        for (RenderInstance model : models) {

            polygons.addAll(model.getPolygonGroups());
        }

        return polygons;
    }

    /**
     * Begins displaying this <code>Render3D</code> instance.
     * @param models <code>Collection&ltRenderInstance&gt;</code>: The models to be rendered.
     */
    public final void render(Collection<RenderInstance> models) {

        setModels(models);
        if (active) {

            return;
        }

        nextTime = System.currentTimeMillis() + 1000;
        active = true;

        repaint();
    }

    /**
     * Assigns the models of this <code>Render3D</code> instance.
     * @param models <code>Collection&ltRenderInstance&gt;</code>: The models to be rendered.
     */
    public final void setModels(Collection<RenderInstance> models) {

        synchronized (renderLock) {

            polygonGroups = getPolygonGroups(models);
        }
    }

    /**
     * Stops displaying this <code>Render3D</code> instance.
     */
    public final void stop() {

        active = false;
    }

    /**
     * Outputs the FPS counter of this <code>Render3D</code> instance.
     * @param outputFPS <code>int</code>: The outputted FPS of this <code>Render3D</code> instance.
     */
    public abstract void outputFPS(int outputFPS);

    /**
     * Retrieves the FPS counter of this <code>Render3D</code> instance.
     * @return <code>int</code>: The <code>fps</code> counter of this <code>Render3D</code> instance.
     */
    public final int getFPS() {

        return fps;
    }
}
