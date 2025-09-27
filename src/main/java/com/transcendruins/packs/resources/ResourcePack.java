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

package com.transcendruins.packs.resources;

import java.util.HashMap;
import java.util.HashSet;

import com.transcendruins.packs.Pack;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>ResourcePack</code>: A class representing the parsed JSON information
 * of a resource set.
 */
public final class ResourcePack extends Pack {

    /**
     * <code>HashMap&lt;Identifier, HashMap&lt;Identifier, Resource&gt;&gt;</code>:
     * The set of all resources stored within the program.
     */
    private static final HashMap<Identifier, HashMap<Identifier, ResourcePack>> RESOURCES = new HashMap<>();

    /**
     * Adds a resource to the set of all resources.
     * 
     * @param resource <code>ResourcePack</code>: The resource to add.
     */
    public static void addResource(ResourcePack resource) {

        Identifier identifier = resource.getIdentifier();

        HashMap<Identifier, ResourcePack> resources = RESOURCES.computeIfAbsent(identifier.toGeneric(),
                _ -> new HashMap<>());
        resources.putIfAbsent(identifier, resource);
    }

    /**
     * Retrieves the set of all resources of a generic ID.
     * 
     * @param id <code>Identifier</code>: The generic id to check for.
     * @return <code>HashSet&lt;Identifier&gt;</code>: The resource identifiers
     *         which match the <code>id</code> parameter.
     */
    public static HashSet<Identifier> getResources(Identifier id) {

        return new HashSet<>(RESOURCES.getOrDefault(id.toGeneric(), new HashMap<>()).keySet());
    }

    public static ResourcePack getResource(Identifier id) {

        return RESOURCES.getOrDefault(id.toGeneric(), new HashMap<>()).get(id);
    }

    /**
     * <code>ResourceSet</code>: The set of resources of this
     * <code>ResourcePack</code> instance.
     */
    private final ResourceSet resources;

    /**
     * Retrieves the set of resources of this <code>ResourcePack</code> instance.
     * 
     * @return <code>ResourceSet</code>: The <code>resources</code> field of this
     *         <code>ResourcePack</code> instance.
     */
    public ResourceSet getResources() {

        return resources;
    }

    /**
     * Creates a new instance of the <code>ResourcePack</code> class using the
     * directory to its root folder.
     * 
     * @param root <code>Path</code>: The directory of the root folder of this
     *             resource.
     */
    public ResourcePack(TracedPath root) throws LoggedException {

        super(root);

        resources = new ResourceSet(getRoot());
    }
}
