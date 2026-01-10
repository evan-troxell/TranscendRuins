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

package com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyException;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>MissingPropertyException</code>: An exception thrown to indicate a
 * missing JSON field.
 */
public final class MissingPropertyException extends PropertyException {

    /**
     * Creates a new instance of the <code>MissingPropertyException</code>
     * exception.
     * 
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The field pathway to record.
     */
    public MissingPropertyException(TracedEntry<?> entry) {

        super(propertyName(entry) + " is undefined.", entry, "Missing Property Exception");
    }
}
