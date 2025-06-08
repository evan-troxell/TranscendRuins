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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.assets.extra.RenderInstance;
import com.transcendruins.graphics3d.geometry.Matrix;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.RenderTriangle;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.ui.Render3D;

/**
 * <code>PolyGroup</code>: A class representing a collection of polygons,
 * grouped together to be rendered simultaneously.
 */
public final class PolyGroup {

    /**
     * <code>int</code>: The maximum number of polygons allowed in a single polygon
     * group.
     */
    public final static int GROUP_POLYGON_CAP = 100;

    /**
     * <code>double</code>: The normalizer value used to curve outliers. This value
     * should be any number between 0.0 and 0.25.
     */
    public final static double DEVIATION_NORMALIZER = 0.1;

    /**
     * <code>Sorter&lt;Triangle&gt;</code>: The sorter used to sort all
     * <code>Triangle</code> instances by the X coordinate of their centers (from
     * lowest to highest).
     */
    public static final Comparator<Triangle> X_SORTER = Comparator.comparing(triangle -> triangle.getCenter().getX());

    /**
     * <code>Sorter&lt;Triangle&gt;</code>: The sorter used to sort all
     * <code>Triangle</code> instances by the Y coordinate of their centers (from
     * lowest to highest).
     */
    public static final Comparator<Triangle> Y_SORTER = Comparator.comparing(triangle -> triangle.getCenter().getY());

    /**
     * <code>Sorter&lt;Triangle&gt;</code>: The sorter used to sort all
     * <code>Triangle</code> instances by the Z coordinate of their centers (from
     * lowest to highest).
     */
    public static final Comparator<Triangle> Z_SORTER = Comparator.comparing(triangle -> triangle.getCenter().getZ());

    /**
     * <code>Map&lt;Triangle, Triangle&gt;</code>: The polygons to construct polygon
     * groups from.
     */
    private final Map<Triangle, Triangle> polygons;

    /**
     * Retrieves the polygons of this <code>PolyGroup</code> instance for the
     * purposes of being rendered.
     * 
     * @return <code>HashSet&lt;RenderTriangle&gt;</code>: The <code>polygons</code>
     *         field of this <code>PolyGroup</code> instance.
     */
    public HashSet<RenderTriangle> getPolygons(Camera3D camera, int frameWidth, int frameHeight) {

        Vector cameraPosition = camera.getPosition();
        Quaternion cameraRotation = camera.getRotation();
        Matrix displayTransform = Render3D.DISPLAY_TRANSFORM.multiply(camera.getZoom());

        Vector positionAdjust = new Vector(frameWidth / 2, frameHeight / 2, 0)
                .subtract(cameraPosition.rotate(cameraRotation).multiply(displayTransform));

        HashSet<RenderTriangle> adjustedPolygons = new HashSet<>();

        for (Triangle poly : polygons.keySet()) {

            Triangle adjusted = poly.rotate(cameraRotation.toConjugate()).multiply(displayTransform)
                    .add(positionAdjust);

            if (adjusted.getMaxX() < 0 || adjusted.getMaxY() < 0 || adjusted.getMinX() >= frameWidth
                    || adjusted.getMinY() >= frameHeight) {

                continue;
            }

            adjustedPolygons.add(new RenderTriangle(adjusted, polygons.get(poly)));
        }

        return adjustedPolygons;
    }

    /**
     * <code>RenderInstance</code>: The render properties of this
     * <code>PolyGroup</code> instance.
     */
    private final RenderInstance render;

    /**
     * Retrieves the render properties of this <code>PolyGroup</code> instance.
     * 
     * @return <code>RenderInstance</code>: The <code>render</code> field of this
     *         <code>PolyGroup</code> instance.
     */
    public RenderInstance getRender() {

        return render;
    }

    /**
     * <code>List&lt;Triangle&gt;</code>: All triangles in this
     * <code>PolyGroup</code> instance, sorted by the X axis.
     */
    private final List<Triangle> sortedX;

    /**
     * <code>List&lt;Triangle&gt;</code>: All triangles in this
     * <code>PolyGroup</code> instance, sorted by the Y axis.
     */
    private final List<Triangle> sortedY;

    /**
     * <code>List&lt;Triangle&gt;</code>: All triangles in this
     * <code>PolyGroup</code> instance, sorted by the Z axis.
     */
    private final List<Triangle> sortedZ;

    /**
     * Creates a new instance of the <code>PolyGroup</code> class.
     * 
     * @param polygons <code>Map&lt;Triangle, Triangle&gt;</code>: The polygons to
     *                 construct polygon groups from.
     * @param render   <code>RenderInstance</code>: The render properties of this
     *                 <code>PolyGroup</code> instance.
     */
    public PolyGroup(Map<Triangle, Triangle> polygons, RenderInstance render) {

        this.polygons = polygons;
        this.render = render;

        sortedX = polygons.keySet().stream().sorted(X_SORTER).collect(Collectors.toList());
        sortedY = polygons.keySet().stream().sorted(Y_SORTER).collect(Collectors.toList());
        sortedZ = polygons.keySet().stream().sorted(Z_SORTER).collect(Collectors.toList());
    }

    /**
     * Recursively subdivides this <code>PolyGroup</code> instance into a list of
     * <code>RenderTriangle</code> instance groups.
     * 
     * @return <code>HashSet&lt;PolyGroup&gt;</code>: The subdivided groups of
     *         <code>RenderTriangle</code> instances.
     */
    public HashSet<PolyGroup> subDivide() {

        HashSet<PolyGroup> polygonGroups = new HashSet<>();
        polygonGroups.add(this);

        if (polygons.size() <= GROUP_POLYGON_CAP) {

            return polygonGroups;
        }

        int normalizedIndex = (int) (polygons.size() * DEVIATION_NORMALIZER);
        int normalizedIndex2 = polygons.size() - normalizedIndex;
        int medianIndex = (int) Math.ceil(polygons.size() / 2);

        double averageDifferenceX = sortedX.get(normalizedIndex2).getCenter().getX()
                - sortedX.get(normalizedIndex).getCenter().getX();
        double averageDifferenceY = sortedY.get(normalizedIndex2).getCenter().getY()
                - sortedY.get(normalizedIndex).getCenter().getY();
        double averageDifferenceZ = sortedZ.get(normalizedIndex2).getCenter().getZ()
                - sortedZ.get(normalizedIndex).getCenter().getZ();

        double greatestAverageDifference = Math.max(averageDifferenceX,
                Math.max(averageDifferenceY, averageDifferenceZ));

        PolyGroup groupA;
        PolyGroup groupB;
        if (greatestAverageDifference == averageDifferenceX) {

            groupA = new PolyGroup(polygonSubset(sortedX.subList(0, medianIndex)), render);
            groupB = new PolyGroup(polygonSubset(sortedX.subList(medianIndex, polygons.size())), render);
        } else if (greatestAverageDifference == averageDifferenceY) {

            groupA = new PolyGroup(polygonSubset(sortedY.subList(0, medianIndex)), render);
            groupB = new PolyGroup(polygonSubset(sortedY.subList(medianIndex, polygons.size())), render);
        } else {

            groupA = new PolyGroup(polygonSubset(sortedZ.subList(0, medianIndex)), render);
            groupB = new PolyGroup(polygonSubset(sortedZ.subList(medianIndex, polygons.size())), render);
        }

        polygonGroups.addAll(groupA.subDivide());
        polygonGroups.addAll(groupB.subDivide());

        return polygonGroups;
    }

    private HashMap<Triangle, Triangle> polygonSubset(Collection<Triangle> copy) {

        HashMap<Triangle, Triangle> subset = new HashMap<>();

        for (Triangle poly : polygons.keySet()) {

            if (copy.contains(poly)) {

                subset.put(poly, polygons.get(poly));
            }
        }

        return subset;
    }
}
