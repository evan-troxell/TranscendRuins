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

package com.transcendruins.resources.styles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.transcendruins.utilities.immutable.ImmutableSet;

public abstract class ComponentProperties {

    private final String type;

    public final String getType() {

        return type;
    }

    private final String id;

    public final String getId() {

        return id;
    }

    private final ImmutableSet<String> classes;

    public final ImmutableSet<String> getClasses() {

        return classes;
    }

    private final ImmutableSet<String> states;

    public final ImmutableSet<String> getStates() {

        return states;
    }

    public ComponentProperties(String type, String id, Set<String> classes, Set<String> states) {
        this.type = type;
        this.id = id;
        this.classes = new ImmutableSet<>(classes);
        this.states = new ImmutableSet<>(states);
    }

    public abstract ComponentProperties getParent();

    public final List<ComponentProperties> getHierarchy() {

        ArrayList<ComponentProperties> hierarchy = new ArrayList<>();
        ComponentProperties current = this;

        while (current != null) {

            hierarchy.add(current);
            current = current.getParent();
        }

        return hierarchy;
    }

    public abstract List<ComponentProperties> getChildren();

    public final ComponentProperties getPrecedingSibling() {

        List<ComponentProperties> preceding = getPrecedingSiblings();
        if (preceding.isEmpty()) {

            return null;
        }

        return preceding.getLast();
    }

    public final List<ComponentProperties> getPrecedingSiblings() {

        ComponentProperties parent = getParent();
        if (parent == null) {

            return List.of();
        }

        List<ComponentProperties> siblings = parent.getChildren();
        int i = siblings.indexOf(this);

        return siblings.subList(0, i);
    }
}