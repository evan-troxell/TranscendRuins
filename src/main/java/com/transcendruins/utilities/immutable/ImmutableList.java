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

package com.transcendruins.utilities.immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * <code>ImmutableList&lt;E&gt;</code>: An <code>ArrayList</code> which is
 * immutable.
 */
public final class ImmutableList<E> extends ArrayList<E> implements Immutable {

    /**
     * <code>boolean</code>: Whether or not this <code>ImmutableList</code> instance
     * has been finalized yet.
     */
    private boolean finalized = false;

    /**
     * Creates a new, empty instance of the <code>ImmutableList</code> class.
     */
    public ImmutableList() {

        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableList</code> class with a single
     * value.
     * 
     * @param val <code>E</code>: The value to add to this
     *            <code>ImmutableList</code> instance.
     */
    public ImmutableList(E val) {

        super(Arrays.asList(val));
        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableList</code> class using a set of
     * values.
     * 
     * @param elements <code>E...</code>: The values to add to this
     *                 <code>ImmutableList</code> instance.
     */
    @SuppressWarnings("unchecked")
    public ImmutableList(E... elements) {

        super(Arrays.asList(elements));
        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableList</code> class using a set of
     * values.
     * 
     * @param collection <code>Collection&lt;E&gt;</code>: The values to add to this
     *                   <code>ImmutableList</code> instance.
     */
    public ImmutableList(Collection<E> collection) {

        super(collection);
        finalized = true;
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean add(E e) {

        raiseError(finalized);
        return super.add(e);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public void add(int index, E element) {

        raiseError(finalized);
        super.add(index, element);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(int index, java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(index, c);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean remove(Object o) {

        raiseError(finalized);
        return super.remove(o);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public E remove(int index) {

        raiseError(finalized);
        return super.remove(index);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.removeAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.retainAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        raiseError(finalized);
        super.clear();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public E set(int index, E element) {

        raiseError(finalized);
        return super.set(index, element);
    }

    /**
     * Creates an immutable iterator which cannot modify this
     * <code>ImmutableList</code> instance.
     * 
     * @return <code>Iterator&lt;E&gt;</code>: The immutable iterator.
     */
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

                raiseError(finalized);
                originalIterator.remove();
            }
        };
    }

    /**
     * Creates an immutable list iterator which cannot modify this
     * <code>ImmutableList</code> instance.
     * 
     * @return <code>ListIterator&lt;E&gt;</code>: The immutable list iterator.
     */
    @Override
    public ListIterator<E> listIterator() {

        ListIterator<E> originalIterator = super.listIterator();
        return createReadOnlyListIterator(originalIterator);
    }

    /**
     * Creates an immutable list iterator which cannot modify this
     * <code>ImmutableList</code> instance, starting at a set index in the list.
     * 
     * @param index <code>int</code>: The index to start at.
     * @return <code>ListIterator&lt;E&gt;</code>: The immutable list iterator.
     */
    @Override
    public ListIterator<E> listIterator(int index) {

        ListIterator<E> originalIterator = super.listIterator(index);
        return createReadOnlyListIterator(originalIterator);
    }

    /**
     * Converts a list iterator into its immutable (read-only) counterpart.
     * 
     * @param originalIterator <code>ListIterator&lt;E&gt;</code>: The mutable list
     *                         iterator.
     * @return <code>ListIterator&lt;E&gt;</code>: The resulting immutable list
     *         iterator.
     */
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

                raiseError(finalized);
                originalIterator.remove();
            }

            @Override
            public void set(E e) {

                raiseError(finalized);
                originalIterator.set(e);
            }

            @Override
            public void add(E e) {

                raiseError(finalized);
                originalIterator.add(e);
            }
        };
    }
}
