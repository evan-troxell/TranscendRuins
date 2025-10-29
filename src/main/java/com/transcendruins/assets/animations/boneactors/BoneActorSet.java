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

package com.transcendruins.assets.animations.boneactors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>BoneActorSet</code>: A class representing a set of bone actors, created
 * from one or more sets merged into a single transformation.
 */
public final class BoneActorSet {

    /**
     * <code>ImmutableMap&lt;String, BoneActor&gt;</code>: The bone actors contained
     * within this <code>BoneActorSet</code> instance.
     */
    private final ImmutableMap<String, BoneActor> boneActors;

    public final BoneActor getBoneActor(String boneActor) {

        return boneActors.get(boneActor);
    }

    /**
     * Creates a new, empty instance of the <code>BoneActorSet</code> class.
     */
    public BoneActorSet() {

        boneActors = new ImmutableMap<>();
    }

    /**
     * Creates a new instance of the <code>BoneActorSet</code> class with duplicate
     * bone actors from another, overridden by bone actors from yet a third.
     * 
     * @param base     <code>BoneActorSet</code>: The base bone actors of this
     *                 <code>BoneActorSet</code> instance.
     * @param override <code>BoneActorSet</code>: The bone actors to override.
     */
    public BoneActorSet(BoneActorSet base, BoneActorSet override) {

        HashMap<String, BoneActor> boneActorsMap = new HashMap<>(base.boneActors);
        boneActorsMap.putAll(override.boneActors);

        boneActors = new ImmutableMap<>(boneActorsMap);
    }

    /**
     * Creates a new instance of the <code>BoneActorSet</code> class.
     * 
     * @param boneActors <code>Map&lt;String, BoneActor&gt;</code>: The bone actors
     *                   of this <code>BoneActorSet</code> instance.
     */
    public BoneActorSet(Map<String, BoneActor> boneActors) {

        this.boneActors = new ImmutableMap<>(boneActors);
    }

    /**
     * Creates a new instance of the <code>BoneActorSet</code> class.
     * 
     * @param sets <code>List&lt;BoneActorSet&gt;</code>: The bone actor sets to
     *             merge.
     */
    public BoneActorSet(List<BoneActorSet> sets) {

        HashMap<String, BoneActor> boneActorsMap = new HashMap<>();

        for (BoneActorSet set : sets) {

            for (Map.Entry<String, BoneActor> actorEntry : set.boneActors.entrySet()) {

                String bone = actorEntry.getKey();
                if (!boneActorsMap.containsKey(bone)) {

                    boneActorsMap.put(bone, actorEntry.getValue());
                    continue;
                }

                boneActorsMap.put(bone, boneActorsMap.get(bone).extend(actorEntry.getValue()));
            }
        }

        boneActors = new ImmutableMap<>(boneActorsMap);
    }

    /**
     * Applies a bone actor from this <code>BoneActorSet</code> instance to a set of
     * vertices.
     * 
     * @param vertices   <code>ArrayList&lt;Vector&gt;</code>: The vertices to apply
     *                   this <code>BoneActorSet</code> instance to.
     * @param bone       <code>String</code>: The bone actor to apply.
     * @param pivotPoint <code>Vector</code>: The origin about which to perform all
     *                   relevant transformations.
     * @return <code>ArrayList&lt;Vector&gt;</code>: The transformed vertices.
     */
    public final ArrayList<Vector> apply(ArrayList<Vector> vertices, String bone, Vector pivotPoint) {

        // If the bone actor does not exist, a new list should still be created - create
        // a default bone actor used only to copy over vertices.
        BoneActor boneActor = boneActors.getOrDefault(bone, BoneActor.DEFAULT);

        ArrayList<Vector> modifiedVertices = new ArrayList<>(vertices.size());
        for (Vector vertex : vertices) {

            modifiedVertices.add(boneActor.transform(vertex, pivotPoint));
        }

        return modifiedVertices;
    }
}
