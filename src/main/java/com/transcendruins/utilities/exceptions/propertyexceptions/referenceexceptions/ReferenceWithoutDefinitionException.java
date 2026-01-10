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
 * <code>ReferenceWithoutDefinitionException</code>: An exception thrown to
 * indicate a reference to an undefined value.
 */
public final class ReferenceWithoutDefinitionException extends PropertyException {

    /**
     * Creates a new instance of the
     * <code>ReferenceWithoutDefinitionException</code> exception.
     * 
     * @param entry          <code>TracedEntry&lt;?&gt;</code>: The undefined
     *                       reference entry.
     * @param definitionType <code>String</code>: The definition type (Identifier,
     *                       Attribute Set, etc.).
     */
    public ReferenceWithoutDefinitionException(TracedEntry<?> entry, String definitionType) {

        super(definitionType + " " + entry.getValue() + " is referenced but cannot be traced.", entry,
                definitionType + " Reference Without Definition Exception");
    }

    /**
     * Creates a new instance of the
     * <code>ReferenceWithoutDefinitionException</code> exception.
     * 
     * @param reference      <code>Object</code>: The undefined reference.
     * @param entry          <code>TracedEntry&lt;?&gt;</code>: The undefined
     *                       reference entry.
     * @param definitionType <code>String</code>: The definition type (Identifier,
     *                       Attribute Set, etc.).
     */
    public ReferenceWithoutDefinitionException(Object reference, TracedEntry<?> entry, String definitionType) {

        super(definitionType + " " + reference + " is referenced but cannot be traced.", entry,
                definitionType + " Reference Without Definition Exception");
    }
}
