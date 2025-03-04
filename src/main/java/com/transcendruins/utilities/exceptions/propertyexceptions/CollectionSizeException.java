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

package com.transcendruins.utilities.exceptions.propertyexceptions;

import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>CollectionSizeException</code>: An exception thrown to indicate a
 * collection has an invalid length.
 */
public final class CollectionSizeException extends PropertyException {

    /**
     * Creates a new instance of the <code>CollectionSizeException</code>
     * exception.
     * 
     * @param entry <code>TracedEntry&lt;TracedArray&gt;</code>: The array
     *              entry to record.
     * @param array <code>TracedArray</code>: The array to record.
     */
    public CollectionSizeException(TracedEntry<TracedArray> entry, TracedArray array) {

        super("%s is the invalid length %d.".formatted(propertyName(entry), array.size()), entry,
                "Array Length Exception");
    }

    /**
     * Creates a new instance of the <code>CollectionSizeException</code>
     * exception.
     * 
     * @param entry <code>TracedEntry&lt;TracedDictionary&gt;</code>: The dictionary
     *              entry to record.
     * @param array <code>TracedDictionary</code>: The dictionary to record.
     */
    public CollectionSizeException(TracedEntry<TracedDictionary> entry, TracedDictionary dict) {

        super("%s is the invalid length %d.".formatted(propertyName(entry), dict.size()), entry,
                "Dictionary Size Exception");
    }
}
