package com.transcendruins.graphics3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.transcendruins.graphics3d.geometry.Triangle3D;
import com.transcendruins.utilities.Sorter;

/**
 * <code>PolyGroup</code>: A class representing a collection of polygons, grouped together to be rendered simultaneously.
 */
public final class PolyGroup {

    /**
     * <code>int</code>: The maximum number of polygons allowed in a single polygon group.
     */
    public final static int GROUP_POLYGON_CAP = 100;

    /**
     * <code>double</code>: The normalizer value used to curve outliers. This value should be any number between 0.0 and 0.25.
     */
    public final static double DEVIATION_NORMALIZER = 0.1;

    /**
     * <code>Sorter&lt;Triangle3D&gt;</code>: The sorter used to sort all <code>Triangle3D</code> instances by the X coordinate of their centers (from lowest to highest).
     */
    public static final Sorter<Triangle3D> CENTER_X_SORTER = new Sorter<Triangle3D>() {

        @Override
        public Triangle3D sortSelector(Triangle3D newEntry, Triangle3D oldEntry) {

            return (newEntry.center.getX() < oldEntry.center.getX()) ? newEntry : oldEntry;
        }
    };

    /**
     * <code>Sorter&lt;Triangle3D&gt;</code>: The sorter used to sort all <code>Triangle3D</code> instances by the Y coordinate of their centers (from lowest to highest).
     */
    public static final Sorter<Triangle3D> CENTER_Y_SORTER = new Sorter<Triangle3D>() {

        @Override
        public Triangle3D sortSelector(Triangle3D newEntry, Triangle3D oldEntry) {

            return (newEntry.center.getY() < oldEntry.center.getY()) ? newEntry : oldEntry;
        }
    };

    /**
     * <code>Sorter&lt;Triangle3D&gt;</code>: The sorter used to sort all <code>Triangle3D</code> instances by the Z coordinate of their centers (from lowest to highest).
     */
    public static final Sorter<Triangle3D> CENTER_Z_SORTER = new Sorter<Triangle3D>() {

        @Override
        public Triangle3D sortSelector(Triangle3D newEntry, Triangle3D oldEntry) {

            return (newEntry.center.getZ() < oldEntry.center.getZ()) ? newEntry : oldEntry;
        }
    };

    /**
     * <code>ArrayList&lt;Triangle3D&gt;</code>: The <code>Triangle3D</code> instances to construct polygon groups from.
     */
    private final ArrayList<Triangle3D> polygons;

    /**
     * <code>List&lt;Triangle3D&gt;</code>: All triangles in this <code>PolyGroup</code> instance, sorted by the X axis.
     */
    private final List<Triangle3D> sortedX;

    /**
     * <code>List&lt;Triangle3D&gt;</code>: All triangles in this <code>PolyGroup</code> instance, sorted by the Y axis.
     */
    private final List<Triangle3D> sortedY;

    /**
     * <code>List&lt;Triangle3D&gt;</code>: All triangles in this <code>PolyGroup</code> instance, sorted by the Z axis.
     */
    private final List<Triangle3D> sortedZ;

    /**
     * Creates a new instance of the <code>PolyGroup</code> class.
     * @param polygons <code>Collection&lt;Triangle3D&gt;</code>: The <code>Triangle3D</code> instances to construct polygon groups from.
     */
    public PolyGroup(Collection<Triangle3D> polygons) {

        this.polygons = new ArrayList<>(polygons);

        sortedX = CENTER_X_SORTER.sort(polygons);
        sortedY = CENTER_Y_SORTER.sort(polygons);
        sortedZ = CENTER_Z_SORTER.sort(polygons);
    }

    /**
     * Recursively subdivides this <code>PolyGroup</code> instance into a list of <code>Triangle3D</code> instance groups.
     * @return <code>ArrayList&lt;PolyGroup&gt;</code>: The subdivided groups of <code>Triangle3D</code> instances.
     */
    public ArrayList<PolyGroup> subDivide() {

        ArrayList<PolyGroup> polygonGroups = new ArrayList<>();
        polygonGroups.add(this);

        if (polygons.size() <= GROUP_POLYGON_CAP) {

            return polygonGroups;
        }

        int normalizedIndex = (int) (polygons.size() * DEVIATION_NORMALIZER);
        int normalizedIndex2 = polygons.size() - normalizedIndex;
        int medianIndex = (int) Math.ceil(polygons.size() / 2);

        double averageDifferenceX = sortedX.get(normalizedIndex2).center.getX() - sortedX.get(normalizedIndex).center.getX();
        double averageDifferenceY = sortedY.get(normalizedIndex2).center.getY() - sortedY.get(normalizedIndex).center.getY();
        double averageDifferenceZ = sortedZ.get(normalizedIndex2).center.getZ() - sortedZ.get(normalizedIndex).center.getZ();

        double greatestAverageDifference = Math.max(averageDifferenceX, Math.max(averageDifferenceY, averageDifferenceZ));

        PolyGroup groupA;
        PolyGroup groupB;
        if (greatestAverageDifference == averageDifferenceX) {

            groupA = new PolyGroup(sortedX.subList(0, medianIndex));
            groupB = new PolyGroup(sortedX.subList(medianIndex, polygons.size()));
        } else if (greatestAverageDifference == averageDifferenceY) {

            groupA = new PolyGroup(sortedY.subList(0, medianIndex));
            groupB = new PolyGroup(sortedY.subList(medianIndex, polygons.size()));
        } else {

            groupA = new PolyGroup(sortedZ.subList(0, medianIndex));
            groupB = new PolyGroup(sortedZ.subList(medianIndex, polygons.size()));
        }

        polygonGroups.addAll(groupA.subDivide());
        polygonGroups.addAll(groupB.subDivide());

        return polygonGroups;
    }

    /**
     * Retrieves the polygons of this <code>PolyGroup</code> instance for the purposes of being rendered.
     * @return <code>ArrayList&lt;Triangle3D&gt;</code>: The <code>polygons</code> field of this <code>PolyGroup</code> instance.
     */
    public ArrayList<Triangle3D> getPolygons() {

        return polygons;
    }
}
