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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <code>ImmutableSet&lt;E&gt;</code>: A <code>Set</code> which is immutable.
 */
public final class ImmutableSet<E> implements Set<E>, Immutable {

    private final HashSet<E> set;

    /**
     * Creates a new, empty instance of the <code>ImmutableSet</code> class.
     */
    public ImmutableSet() {

        set = new HashSet<>();
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class with a single
     * value.
     * 
     * @param val <code>E</code>: The value to add to this <code>ImmutableSet</code>
     *            instance.
     */
    public ImmutableSet(E val) {

        this(Arrays.asList(val));
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class using a set of
     * values.
     * 
     * @param elements <code>E...</code>: The values to add to this
     *                 <code>ImmutableSet</code> instance.
     */
    public ImmutableSet(E... elements) {

        this(Arrays.asList(elements));
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class using a set of
     * values.
     * 
     * @param collection <code>Collection&lt;E&gt;</code>: The values to add to this
     *                   <code>ImmutableSet</code> instance.
     */
    public ImmutableSet(Collection<E> collection) {

        set = new HashSet<>(collection);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean add(E e) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean remove(Object o) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        throw raiseError();
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        throw raiseError();
    }

    /**
     * Creates an immutable iterator which cannot modify this
     * <code>ImmutableSet</code> instance.
     * 
     * @return <code>Iterator&lt;E&gt;</code>: The immutable iterator.
     */
    @Override
    public Iterator<E> iterator() {

        Iterator<E> originalIterator = set.iterator();
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

    @Override
    public int size() {

        return set.size();
    }

    @Override
    public boolean isEmpty() {

        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        return set.contains(o);
    }

    @Override
    public Object[] toArray() {

        return set.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(T[] a) {

        return set.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {

        return set.containsAll(c);
    }
}
