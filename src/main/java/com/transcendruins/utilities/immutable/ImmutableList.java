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
import java.util.List;
import java.util.ListIterator;

/**
 * <code>ImmutableList&lt;E&gt;</code>: A <code>List</code> which is immutable.
 */
public final class ImmutableList<E> implements List<E>, Immutable {

    private final ArrayList<E> list;

    /**
     * Creates a new, empty instance of the <code>ImmutableList</code> class.
     */
    public ImmutableList() {

        list = new ArrayList<>();
    }

    /**
     * Creates a new instance of the <code>ImmutableList</code> class with a single
     * value.
     * 
     * @param val <code>E</code>: The value to add to this
     *            <code>ImmutableList</code> instance.
     */
    public ImmutableList(E val) {

        this(Arrays.asList(val));
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

        this(Arrays.asList(elements));
    }

    /**
     * Creates a new instance of the <code>ImmutableList</code> class using a set of
     * values.
     * 
     * @param collection <code>Collection&lt;E&gt;</code>: The values to add to this
     *                   <code>ImmutableList</code> instance.
     */
    public ImmutableList(Collection<E> collection) {

        list = new ArrayList<>(collection);
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean add(E e) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public void add(int index, E element) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(int index, java.util.Collection<? extends E> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean remove(Object o) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public E remove(int index) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableList</code> class and
     * will raise an exception.
     */
    @Override
    public E set(int index, E element) {

        throw raiseError();
    }

    /**
     * Creates an immutable iterator which cannot modify this
     * <code>ImmutableList</code> instance.
     * 
     * @return <code>Iterator&lt;E&gt;</code>: The immutable iterator.
     */
    @Override
    public Iterator<E> iterator() {

        Iterator<E> originalIterator = list.iterator();

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

                throw raiseError();
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

        ListIterator<E> originalIterator = list.listIterator();
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

        ListIterator<E> originalIterator = list.listIterator(index);
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

                throw raiseError();
            }

            @Override
            public void set(E e) {

                throw raiseError();
            }

            @Override
            public void add(E e) {

                throw raiseError();
            }
        };
    }

    @Override
    public int size() {

        return list.size();
    }

    @Override
    public boolean isEmpty() {

        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        return list.contains(o);
    }

    @Override
    public Object[] toArray() {

        return list.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(T[] a) {

        return list.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {

        return list.containsAll(c);
    }

    @Override
    public E get(int index) {

        return list.get(index);
    }

    @Override
    public int indexOf(Object o) {

        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {

        return list.lastIndexOf(o);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {

        return new ImmutableList<>(list.subList(fromIndex, toIndex));
    }
}
