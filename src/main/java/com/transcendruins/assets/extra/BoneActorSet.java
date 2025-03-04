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

package com.transcendruins.assets.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.transcendruins.graphics3d.geometry.Vector;
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
     * @param sets <code>BoneActorSet...</code>: The bone actor sets to
     *             merge.
     */
    public BoneActorSet(BoneActorSet... sets) {

        this(List.of(sets));
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
     *                   this
     *                   <code>BoneActorSet</code> instance to.
     * @param bone       <code>String</code>: The bone actor to apply.
     * @param pivotPoint <code>Vector</code>: The origin about which to perform all
     *                   relevant transformations.
     * @return <code>ArrayList&lt;Vector&gt;</code>: The transformed vertices.
     */
    public ArrayList<Vector> apply(ArrayList<Vector> vertices, String bone, Vector pivotPoint) {

        // If the bone actor does not exist, a new list should still be created - create
        // a default bone actor used only to copy over vertices.
        BoneActor boneActor = boneActors.getOrDefault(bone, new BoneActor());

        ArrayList<Vector> modifiedVertices = new ArrayList<>(vertices.size());
        for (Vector vertex : vertices) {

            modifiedVertices.add(boneActor.transform(vertex, pivotPoint));
        }

        return modifiedVertices;
    }

    /**
     * Applies a bone actor from this <code>BoneActorSet</code> instance to a set of
     * vertices.
     * 
     * @param vertices   <code>HashMap&lt;Integer, HashMap&lt;Vector, Double&gt;&gt;</code>:
     *                   The vertices to apply this
     *                   <code>BoneActorSet</code> instance to.
     * @param bone       <code>String</code>: The bone actor to apply.
     * @param pivotPoint <code>Vector</code>: The origin about which to perform all
     *                   relevant transformations.
     * @return <code>ImmutableMap&lt;Integer, ImmutableMap&lt;Vector, Double&gt;&gt;</code>:
     *         The transformed vertices.
     */
    public HashMap<Integer, HashMap<Vector, Double>> apply(
            Map<Integer, ? extends Map<Vector, Double>> vertices,
            String bone, Vector pivotPoint) {

        // If the bone actor does not exist, a new set should still be created - create
        // a default bone actor used only to copy over vertices.
        BoneActor boneActor = boneActors.getOrDefault(bone, new BoneActor());
        HashMap<Integer, HashMap<Vector, Double>> modifiedVertices = new HashMap<>();

        for (Map.Entry<Integer, ? extends Map<Vector, Double>> boneWeightsEntry : vertices.entrySet()) {

            int index = boneWeightsEntry.getKey();
            HashMap<Vector, Double> verticesMap = new HashMap<>();

            for (Map.Entry<Vector, Double> vertexEntry : boneWeightsEntry.getValue().entrySet()) {

                double weight = vertexEntry.getValue();
                Vector vertex = boneActor.transform(vertexEntry.getKey(), pivotPoint);

                verticesMap.put(vertex, weight);
            }

            modifiedVertices.put(index, verticesMap);
        }

        return modifiedVertices;
    }
}
