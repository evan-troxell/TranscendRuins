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

package com.transcendruins.graphics3d;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>RenderedPixel</code>: A class representing a pixel in a rendered 3D
 * image which contains all polygons that have been
 */
public final class RenderedPixel {

    /**
     * <code>Sortert&lt;Double&gt;</code>: The sorter used to sort depths in a
     * <code>RenderedPixel</code> instance (from lowest to highest).
     */
    private static final Comparator<PixelDepth> DEPTH_SORTER = Comparator.comparing(pixel -> pixel.depth);

    /**
     * <code>int</code>: The X coordinate of this <code>RenderedPixel</code>
     * instance.
     */
    private final int x;

    /**
     * Retrieves the X coordinate of this <code>RenderedPixel</code> instance.
     * 
     * @return <code>int</code>: The <code>x</code> field of this
     *         <code>RenderedPixel</code> instance.
     */
    public int getX() {

        return x;
    }

    /**
     * <code>int</code>: The Y coordinate of this <code>RenderedPixel</code>
     * instance.
     */
    private final int y;

    /**
     * Retrieves the Y coordinate of this <code>RenderedPixel</code> instance.
     * 
     * @return <code>int</code>: The <code>y</code> field of this
     *         <code>RenderedPixel</code> instance.
     */
    public int getY() {

        return y;
    }

    /**
     * <code>double</code>: The maximum depth of this <code>RenderedPixel</code>
     * instance. This is only applicable if the <code>isSolid</code> field is
     * <code>true</code>.
     */
    private double minDepth = Double.NEGATIVE_INFINITY;

    /**
     * <code>LinkedList&lt;PixelDepth&gt;</code>: A map of all polygon depths in
     * this <code>RenderedPixel</code> instance, paired with the color associated
     * with the polygon.
     */
    private final LinkedList<PixelDepth> depths = new LinkedList<>();

    /**
     * Creates a new instance of the <code>RenderedPixel</code> class.
     * 
     * @param x     <code>int</code>: The X coordinate of this
     *              <code>RenderedPixel</code> instance.
     * @param y     <code>int</code>: The Y coordinate of this
     *              <code>RenderedPixel</code> instance.
     * @param depth <code>double</code>: The depth of the polygon.
     * @param rgba  <code>int</code>: The RGBA color associated with the polygon.
     */
    public RenderedPixel(int x, int y, double depth, int rgba) {

        this.x = x;
        this.y = y;
        addDepth(depth, rgba);
    }

    /**
     * Adds a depth-color to this <code>RenderedPixel</code> instance.
     * 
     * @param depth <code>double</code>: The depth of the polygon.
     * @param rgba  <code>int</code>: The RGBA color associated with the polygon.
     */
    public synchronized void addDepth(double depth, int rgba) {

        if (depth < minDepth) {

            return;
        }

        PixelDepth pixel = new PixelDepth(depth, rgba);
        depths.add(pixel);
    }

    /**
     * Retrieves the rendered color value of this <code>RenderedPixel</code>
     * instance.
     * 
     * @param background <code>int</code>: The background RGBA color to apply.
     * @return <code>int</code>: The resulting RGBA color.
     */
    public synchronized int getColor(int background) {

        List<PixelDepth> depthsSorted = depths.stream().sorted(DEPTH_SORTER).toList();
        if (depthsSorted.isEmpty()) {

            return background;
        }

        Iterator<PixelDepth> iterator = depthsSorted.iterator();

        int prevColor = iterator.next().rgba;
        while (iterator.hasNext()) {

            PixelDepth depth = iterator.next();
            if (depth.depth < minDepth) {

                break;
            }
            prevColor = mergeRGB(prevColor, depth.rgba);
        }

        return mergeRGB(prevColor, background);
    }

    /**
     * Merges the RGBA values of two color channels into a single one, using one as
     * the foreground color and the other as the background color. This method will
     * combine the alpha values such that the background alpha value will be limited
     * to the maximum value of (1.0 - foregroundAlpha). The background RGB channels
     * will be merged to the foreground RGB channels according to the remaining
     * alpha values.
     * 
     * @param foregroundRGBA <code>int</code>: The RGBA color associated with the
     *                       foreground pixel.
     * @param backgroundRGBA <code>int</code>: The RGBA color associated with the
     *                       background pixel.
     * @return <code>int</code>: The RGBA color resulting from the merge.
     */
    private int mergeRGB(int foregroundRGBA, int backgroundRGBA) {

        int foregroundAlpha = (foregroundRGBA >> 24) & 0xFF;
        int backgroundAlpha = (backgroundRGBA >> 24) & 0xFF;
        int nextPct = (backgroundAlpha * (255 - foregroundAlpha)) / 255;

        int outAlpha = foregroundAlpha + nextPct;

        if (outAlpha == 0) {

            return 0;
        }

        int outRed = (foregroundAlpha * ((foregroundRGBA >> 16) & 0xFF) + nextPct * ((backgroundRGBA >> 16) & 0xFF))
                / outAlpha;
        int outGreen = (foregroundAlpha * ((foregroundRGBA >> 8) & 0xFF) + nextPct * ((backgroundRGBA >> 8) & 0xFF))
                / outAlpha;
        int outBlue = (foregroundAlpha * (foregroundRGBA & 0xFF) + nextPct * (backgroundRGBA & 0xFF)) / outAlpha;

        return (outAlpha << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
    }

    /**
     * <code>PixelDepth</code>: The depth of a color on an individual pixel in a
     * <code>RenderedPixel</code> instance.
     */
    private final class PixelDepth {

        /**
         * <code>double</code>: The depth of this <code>PixelDepth</code> instance.
         */
        private final double depth;

        /**
         * <code>int</code>: The RGBA color associated with the polygon.
         */
        private final int rgba;

        /**
         * Creates a new instance of the <code>PixelDepth</code> class.
         * 
         * @param depth <code>double</code>: The depth of the polygon.
         * @param rgba  <code>int</code>: The RGBA color associated with the polygon.
         */
        private PixelDepth(double depth, int rgba) {

            this.depth = depth;
            this.rgba = rgba;

            if (((rgba >> 24) & 0xFF) == 255)
                minDepth = depth;
        }
    }
}
