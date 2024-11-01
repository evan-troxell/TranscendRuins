package com.transcendruins.rendering;

import java.util.concurrent.Callable;

import com.transcendruins.geometry.MatrixOperations;
import com.transcendruins.geometry.PolyGroup;
import com.transcendruins.ui.Render3D;

/**
 * <code>RenderThread</code>: A class representing a thread which can be sent polygon lists to render, used to assist in rendering a main image.
 */
public final class RenderTask implements Callable<Void> {

    /**
     * <code>PolyGroup</code>: The polygon group to render.
     */
    private final PolyGroup polygonGroup;

    /**
     * <code>MatrixOperations</code>: The vertice normalizer used to adjust for the spacial position and orientation of the camera.
     */
    private final MatrixOperations verticeNormalizer;

    /**
     * <code>RenderedPixel[]</code>: An array of the mapped pixel distances to add onto.
     */
    private final RenderedPixel[] pixelDepths;

    /**
     * <code>int</code>: The width of the frame to center on.
     */
    private final int width;

    /**
     * <code>int</code>: The height of the frame to center on.
     */
    private final int height;

    /**
     * Creates a new instance of the <code>RenderThread</code> class.
     * @param polygonGroup <code>PolyGroup</code>: The polygon group to render.
     * @param verticeNormalizer <code>MatrixOperations</code>: The vertice normalizer used to adjust for the spacial position and orientation of the camera.
     * @param pixelDepths <code>RenderedPixel[]</code>: An array of the mapped pixel distances to add onto.
     * @param width <code>int</code>: The width of the frame to center on.
     * @param height <code>int</code>: The height of the frame to center on.
     */
    public RenderTask(PolyGroup polygonGroup, MatrixOperations verticeNormalizer, RenderedPixel[] pixelDepths, int width, int height) {

        this.polygonGroup = polygonGroup;
        this.verticeNormalizer = verticeNormalizer;
        this.pixelDepths = pixelDepths;
        this.width = width;
        this.height = height;
    }

    /**
     * Renders this <code>RenderTask</code> instance.
     */
    @Override
    public Void call() {

        Render3D.renderPolygonGroup(polygonGroup, verticeNormalizer, pixelDepths, width, height);
        return null;
    }
}
