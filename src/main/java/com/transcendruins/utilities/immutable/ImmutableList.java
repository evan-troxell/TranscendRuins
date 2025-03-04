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

    private boolean finalized = false;

    public ImmutableList() {

        finalized = true;
    }

    public ImmutableList(E val) {

        super(Arrays.asList(val));
        finalized = true;
    }

    @SuppressWarnings("unchecked")
    public ImmutableList(E... elements) {

        super(Arrays.asList(elements));
        finalized = true;
    }

    public ImmutableList(Collection<E> collection) {

        super(collection);
        finalized = true;
    }

    @Override
    public boolean add(E e) {

        raiseError(finalized);
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {

        raiseError(finalized);
        super.add(index, element);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends E> c) {

        raiseError(finalized);
        return super.addAll(index, c);
    }

    @Override
    public boolean remove(Object o) {

        raiseError(finalized);
        return super.remove(o);
    }

    @Override
    public E remove(int index) {

        raiseError(finalized);
        return super.remove(index);
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
    public E set(int index, E element) {

        raiseError(finalized);
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

                raiseError(finalized);
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
