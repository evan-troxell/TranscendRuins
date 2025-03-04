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
 * <code>ImmutableSet&lt;E&gt;</code>: A set which can be declared 'final',
 * after which no modifications can be made.
 */
public final class ImmutableSet<E> extends HashSet<E> implements Immutable {

    private boolean finalized = false;

    public ImmutableSet() {

        finalized = true;
    }

    @SuppressWarnings("unchecked")
    public ImmutableSet(E... elements) {

        super(Arrays.asList(elements));
        finalized = true;
    }

    public ImmutableSet(Collection<E> collection) {

        super(collection);
        finalized = true;
    }

    @Override
    public boolean add(E e) {

        raiseError(finalized);
        return super.add(e);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(c);
    }

    @Override
    public boolean remove(Object o) {

        raiseError(finalized);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {

        raiseError(finalized);
        return super.retainAll(c);
    }

    @Override
    public void clear() {

        raiseError(finalized);
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

                raiseError(finalized);
                originalIterator.remove();
            }
        };
    }
}
