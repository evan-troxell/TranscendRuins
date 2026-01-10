/* Copyright 2026 Evan Troxell
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

package com.transcendruins.assets.modelassets.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.selection.WeightedRoll;

/**
 * <code>ElementAttributes</code>: A class which represents the attributes of an
 * <code>ElementSchema</code> instance.
 */
public final class ElementAttributes extends PrimaryAssetAttributes {

    private final Dimension tileDimensions;

    public final Dimension getTileDimensions() {

        return tileDimensions != null ? new Dimension(tileDimensions) : null;
    }

    private final WeightedRoll<Color> mapColor;

    public final WeightedRoll<Color> getMapColor() {

        return mapColor;
    }

    /**
     * Compiles this <code>ElementAttributes</code> instance into a completed
     * instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ElementAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to compile
     *               this <code>ElementAttributes</code> instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ElementAttributes</code> instance is the base attribute
     *               set of an <code>ElementSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while processing
     *                         this <code>ElementAttributes</code> instance.
     */
    public ElementAttributes(AssetSchema schema, TracedDictionary json, boolean isBase) throws LoggedException {

        super(schema, json, isBase);

        tileDimensions = json.get("tileDimensions", List.of(

                json.arrayCase(entry -> {

                    TracedArray tileDimensionsJson = entry.getValue();

                    TracedEntry<Integer> widthEntry = tileDimensionsJson.getAsInteger(0, false, null, num -> num > 0);
                    int width = widthEntry.getValue();

                    TracedEntry<Integer> lengthEntry = tileDimensionsJson.getAsInteger(1, false, null, num -> num > 0);
                    int length = lengthEntry.getValue();

                    return new Dimension(width, length);
                }), json.dictCase(entry -> {

                    TracedDictionary tileDimensionsJson = entry.getValue();

                    TracedEntry<Integer> widthEntry = tileDimensionsJson.getAsInteger("width", false, null,
                            num -> num > 0);
                    int width = widthEntry.getValue();

                    TracedEntry<Integer> lengthEntry = tileDimensionsJson.getAsInteger("length", false, null,
                            num -> num > 0);
                    int length = lengthEntry.getValue();

                    return new Dimension(width, length);
                }), json.nullCase(_ -> null)));

        mapColor = json.getAsRoll("mapColor", true, null, "mapColor", json.colorCase(TracedEntry::getValue));
    }
}
