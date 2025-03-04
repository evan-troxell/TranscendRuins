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

package com.transcendruins.utilities.exceptions.propertyexceptions.identifierexceptions;

import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyException;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>DuplicateIdentifierException</code>: An exception thrown to indicate a
 * duplicate object identifier.
 */
public final class DuplicateIdentifierException extends PropertyException {

    /**
     * Creates a new instance of the <code>DuplicateIdentifierException</code>
     * exception.
     * 
     * @param entry <code>TracedEntry&lt;Identifier&gt;</code>: The duplicate
     *              identifier which caused the exception to be thrown.
     */
    public DuplicateIdentifierException(TracedEntry<Identifier> entry) {

        super("\'" + entry + "\' is a duplicate identifier.", entry, "Duplicate Identifier Exception");
    }
}
