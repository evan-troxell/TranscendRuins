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

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;

public final class LayoutDimension {

    private final int width;

    public int getWidth() {

        return width;
    }

    private final int length;

    public int getLength() {

        return length;
    }

    public LayoutDimension(TracedCollection collection, Object key, boolean variableSizeAllowed)
            throws LoggedException {

        int[] dimensions = collection.get(key, List.of(

                collection.arrayCase(entry -> {

                    TracedArray sizeJson = entry.getValue();
                    if (sizeJson.size() != 2) {

                        throw new CollectionSizeException(entry, sizeJson);
                    }

                    return new int[] { sizeJson.getAsInteger(0, false, null, num -> num >= 1).getValue(),
                            sizeJson.getAsInteger(1, false, null, num -> num >= 1).getValue() };
                }),

                collection.dictCase(entry -> {

                    TracedDictionary sizeJson = entry.getValue();

                    return new int[] { sizeJson.getAsInteger("width", false, null, num -> num >= 1).getValue(),
                            sizeJson.getAsInteger("length", false, null, num -> num >= 1).getValue(), };
                })));

        width = dimensions[0];
        length = dimensions[1];
    }
}
