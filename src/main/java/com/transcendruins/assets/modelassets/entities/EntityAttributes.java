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

package com.transcendruins.assets.modelassets.entities;

import java.util.List;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.modelassets.attack.AttackSchema;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>EntityAttributes</code>: A class which represents the attributes of an
 * <code>EntitySchema</code> instance.
 */
public final class EntityAttributes extends PrimaryAssetAttributes {

    public final record DoubleDimension(double width, double length) {

        public static final DoubleDimension DEFAULT = new DoubleDimension(1.0, 1.0);
    }

    private final DoubleDimension tileDimensions;

    public final DoubleDimension getTileDimensions() {

        return tileDimensions;
    }

    private final AttackSchema attack;

    public final AttackSchema getAttack() {

        return attack;
    }

    /**
     * Compiles this <code>EntityAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>EntityAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>EntityAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>EntityAttributes</code> instance is the base attribute
     *               set of an <code>EntitySchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>EntityAttributes</code> instance.
     */
    public EntityAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        tileDimensions = json.get("tileDimensions", List.of(json.arrayCase(entry -> {

            TracedArray tileDimensionsJson = entry.getValue();

            TracedEntry<Double> widthEntry = tileDimensionsJson.getAsDouble(0, false, null, num -> num > 0);
            double width = widthEntry.getValue();

            TracedEntry<Double> lengthEntry = tileDimensionsJson.getAsDouble(1, false, null, num -> num > 0);
            double length = lengthEntry.getValue();

            return new DoubleDimension(width, length);
        }), json.dictCase(entry -> {

            TracedDictionary tileDimensionsJson = entry.getValue();

            TracedEntry<Double> widthEntry = tileDimensionsJson.getAsDouble("width", false, null, num -> num > 0);
            double width = widthEntry.getValue();

            TracedEntry<Double> lengthEntry = tileDimensionsJson.getAsDouble("length", false, null, num -> num > 0);
            double length = lengthEntry.getValue();

            return new DoubleDimension(width, length);
        }), json.nullCase(_ -> null)));

        attack = AttackSchema.createAttack(json, isBase);
    }
}
