package com.transcendruins.utilities;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class which can utilized to sort any collection of type <code>K</code> depending on the implementation of the <code>sortSelector</code> method.
 */
public abstract class Sorter<K> {

    /**
     * Creates a new instance of the <code>CollectionOperator&lt;K&gt;</code> class.
     */
    public Sorter() {}

    /**
     * Sorts a collection using the <code>selectedValue</code> method to determine entry priority in the return list.
     * @param c <code>Collection&lt;K&gt;</code>: The collection to sort.
     * @return <code>ArrayList&lt;K&gt;</code>: The sorted return list.
     */
    public final ArrayList<K> sort(Collection<K> c) {

        return sort(new ArrayList<>(c),
        0, c.size() - 1);
    }

    /**
     * Sorts an <code>ArrayList</code> instance using the <code>selectedValue</code> method to determine entry priority in the return list. This variant will only sort entries between the <code>left</code> perameter and the <code>right</code> perameter.
     * @param c <code>ArrayList&lt;K&gt;</code>: The array to sort.
     * @param left <code>int</code>: The minimum bounds.
     * @param right <code>int</code>: The maximum bounds.
     * @return <code>ArrayList&lt;K&gt;</code>: The sorted return list.
     */
    private ArrayList<K> sort(ArrayList<K> c, int left, int right) {

        if (left < right) {

            int partI = partition(c, left, right);

            sort(c, left, partI - 1);
            sort(c, partI + 1, right);
        }

        return c;
    }

    /**
     * Partitions an <code>ArrayList</code> instance into 2 parts, given minimum and maximum bounds.
     * @param c <code>ArrayList&lt;K&gt;</code>: The array to partitions.
     * @param left <code>int</code>: The minimum bounds.
     * @param right <code>int</code>: The maximum bounds.
     * @return <code>int</code>: The index of the partition.
     */
    private int partition(ArrayList<K> c, int left, int right) {

        K pivot = c.get(right);

        int i = left - 1;

        for (int j = left; j <= right - 1; j++) {

            K val = c.get(j);

            if (sortSelector(pivot, val) == val) {

                i++;
                c.set(j, c.get(i));
                c.set(i, val);
            }
        }

        c.set(right, c.get(i + 1));
        c.set(i + 1, pivot);

        return i + 1;
    }

    /**
     * The method by which the <code>sort</code> method determines entry priority in the finalized sorted list.
     * @param newEntry <code>K</code>: The new entry to compare.
     * @param oldEntry <code>K</code>: The old entry to compare.
     * @return <code>K</code>: The selected entry.
     */
    public abstract K sortSelector(K newEntry, K oldEntry);
}
