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

import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>StringLengthException</code>: An exception thrown to indicate a
 * <code>String</code> has an invalid length.
 */
public final class StringLengthException extends PropertyException {

    /**
     * Creates a new instance of the <code>StringLengthException</code> exception.
     * 
     * @param entry <code>TracedEntry&lt;String&gt;</code>: The invalid string.
     */
    public StringLengthException(TracedEntry<String> entry) {

        super(propertyName(entry) + " is the invalid length " + entry.getValue().length() + ".", entry,
                "String Length Exception");
    }
}
