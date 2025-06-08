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

/**
 * <code>ImmutableSet&lt;E&gt;</code>: A <code>HashSet</code> which is
 * immutable.
 */
public final class ImmutableSet<E> extends HashSet<E> implements Immutable {

    /**
     * <code>boolean</code>: Whether or not this <code>ImmutableSet</code> instance
     * has been finalized yet.
     */
    private boolean finalized = false;

    /**
     * Creates a new, empty instance of the <code>ImmutableSet</code> class.
     */
    public ImmutableSet() {

        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class with a single
     * value.
     * 
     * @param val <code>E</code>: The value to add to this <code>ImmutableSet</code>
     *            instance.
     */
    public ImmutableSet(E val) {

        super(Arrays.asList(val));
        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class using a set of
     * values.
     * 
     * @param elements <code>E...</code>: The values to add to this
     *                 <code>ImmutableSet</code> instance.
     */
    @SuppressWarnings("unchecked")
    public ImmutableSet(E... elements) {

        super(Arrays.asList(elements));
        finalized = true;
    }

    /**
     * Creates a new instance of the <code>ImmutableSet</code> class using a set of
     * values.
     * 
     * @param collection <code>Collection&lt;E&gt;</code>: The values to add to this
     *                   <code>ImmutableSet</code> instance.
     */
    public ImmutableSet(Collection<E> collection) {

        super(collection);
        finalized = true;
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean add(E e) {

        raiseError(finalized);
        return super.add(e);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean remove(Object o) {

        raiseError(finalized);
        return super.remove(o);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.removeAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.retainAll(c);
    }

    /**
     * This method is not implemented in the <code>ImmutableSet</code> class and
     * will raise an exception.
     */
    @Override
    public void clear() {

        raiseError(finalized);
        super.clear();
    }

    /**
     * Creates an immutable iterator which cannot modify this
     * <code>ImmutableSet</code> instance.
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
}
