package com.transcendruins.utilities.finalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * <code>FinalizedList&lt;E&gt;</code>: An ArrayList which can be declared 'final', after which no modifications can be made.
 */
public class FinalizedList<E> extends ArrayList<E> implements Finalized {

    /**
     * <code>boolean</code>: Whether or not this <code>FinalizedList</code> instance has been declared final.
     */
    private boolean isFinalized = false;

    public FinalizedList() {}

    public FinalizedList(int initialCapacity) {

        super(initialCapacity);
    }

    public FinalizedList(Collection<E> elements) {

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
    public void add(int index, E element) {

        checkFinalized(isFinalized);
        super.add(index, element);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        checkFinalized(isFinalized);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends E> c) {

        checkFinalized(isFinalized);
        return super.addAll(index, c);
    }

    @Override
    public boolean remove(Object o) {

        checkFinalized(isFinalized);
        return super.remove(o);
    }

    @Override
    public E remove(int index) {

        checkFinalized(isFinalized);
        return super.remove(index);
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
    public E set(int index, E element) {

        checkFinalized(isFinalized);
        return super.set(index, element);
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

    @Override
    public ListIterator<E> listIterator() {
        ListIterator<E> originalIterator = super.listIterator();
        return createReadOnlyListIterator(originalIterator);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ListIterator<E> originalIterator = super.listIterator(index);
        return createReadOnlyListIterator(originalIterator);
    }

    private ListIterator<E> createReadOnlyListIterator(ListIterator<E> originalIterator) {
        return new ListIterator<>() {
            @Override
            public boolean hasNext() {
                return originalIterator.hasNext();
            }

            @Override
            public E next() {
                return originalIterator.next();
            }

            @Override
            public boolean hasPrevious() {
                return originalIterator.hasPrevious();
            }

            @Override
            public E previous() {
                return originalIterator.previous();
            }

            @Override
            public int nextIndex() {
                return originalIterator.nextIndex();
            }

            @Override
            public int previousIndex() {
                return originalIterator.previousIndex();
            }

            @Override
            public void remove() {
                checkFinalized(isFinalized);
                originalIterator.remove();
            }

            @Override
            public void set(E e) {
                checkFinalized(isFinalized);
                originalIterator.set(e);
            }

            @Override
            public void add(E e) {
                checkFinalized(isFinalized);
                originalIterator.add(e);
            }
        };
    }
}
