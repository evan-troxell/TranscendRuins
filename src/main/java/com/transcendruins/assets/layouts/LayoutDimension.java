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

package com.transcendruins.assets.layouts;

import java.util.List;

import com.transcendruins.assets.extra.Range;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.world.World;

public final class LayoutDimension {

    private final Range width;

    public int getWidth(World world) {

        return width.getIntegerValue(world.nextRandom());
    }

    private final Range height;

    public int getHeight(World world) {

        return height.getIntegerValue(world.nextRandom());
    }

    public LayoutDimension(TracedCollection collection, Object key, boolean variableSizeAllowed)
            throws LoggedException {

        Range[] dimensions = collection.get(key, List.of(

                collection.arrayCase(entry -> {

                    TracedArray sizeJson = entry.getValue();
                    if (sizeJson.size() != 2) {

                        throw new CollectionSizeException(entry, sizeJson);
                    }

                    return new Range[] {
                            Range.createRange(sizeJson, 0, false, variableSizeAllowed, num -> num >= 1),
                            Range.createRange(sizeJson, 1, false, variableSizeAllowed, num -> num >= 1)
                    };
                }),

                collection.dictCase(entry -> {

                    TracedDictionary sizeJson = entry.getValue();

                    return new Range[] {
                            Range.createRange(sizeJson, "width", false, variableSizeAllowed, num -> num >= 1),
                            Range.createRange(sizeJson, "height", false, variableSizeAllowed, num -> num >= 1)
                    };
                })));

        width = dimensions[0];
        height = dimensions[1];
    }
}
