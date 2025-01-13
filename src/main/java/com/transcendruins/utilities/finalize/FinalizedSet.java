package com.transcendruins.utilities.finalize;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <code>FinalizedSet&lt;E&gt;</code>: A set which can be declared 'final', after which no modifications can be made.
 */
public class FinalizedSet<E> extends HashSet<E> implements Finalized {

    /**
     * <code>boolean</code>: Whether or not this <code>FinalizedSet</code> instance has been declared final.
     */
    private boolean isFinalized = false;

    public FinalizedSet() {}

    public FinalizedSet(int initialCapacity) {

        super(initialCapacity);
    }

    public FinalizedSet(int initialCapacity, float loadFactor) {

        super(initialCapacity, loadFactor);
    }

    public FinalizedSet(Collection<E> elements) {

        super(elements);
    }

    @Override
    public void finalizeData() {

        if (isFinalized) {

            return;
        }

        isFinalized = true;

        for (E value : this) {

            if (value instanceof Finalized finalValue) {

                finalValue.finalizeData();
            }
        }
    }

    @Override
    public boolean add(E e) {

        checkFinalized(isFinalized);
        return super.add(e);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        checkFinalized(isFinalized);
        return super.addAll(c);
    }

    @Override
    public boolean remove(Object o) {

        checkFinalized(isFinalized);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        checkFinalized(isFinalized);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        checkFinalized(isFinalized);
        return super.retainAll(c);
    }

    @Override
    public void clear() {

        checkFinalized(isFinalized);
        super.clear();
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> originalIterator = super.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return originalIterator.hasNext();
            }

            @Override
            public E next() {
                return originalIterator.next();
            }

            @Override
            public void remove() {
                checkFinalized(isFinalized);
                originalIterator.remove();
            }
        };
    }
}
