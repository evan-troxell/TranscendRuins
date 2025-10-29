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

package com.transcendruins.world;

import java.util.HashSet;

import com.transcendruins.assets.modelassets.elements.ElementInstance;

public final class AreaTile {

    private final HashSet<ElementInstance> elements = new HashSet<>();

    public final void addElement(ElementInstance element) {

        elements.add(element);
    }

    public final void removeElement(ElementInstance element) {

        elements.remove(element);
    }

    public final HashSet<ElementInstance> getElements() {

        return new HashSet<>(elements);
    }

    public AreaTile() {
    }
}
