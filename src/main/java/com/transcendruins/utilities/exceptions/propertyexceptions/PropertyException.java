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

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>PropertyException</code>: A general exception thrown to indicate an
 * error concerning a JSON field This is a general exception, and should be
 * inherited upon to improve clarity.
 */
public class PropertyException extends LoggedException {

    /**
     * Creates a new instance of the <code>PropertyException</code> exception.
     * 
     * @param message   <code>String</code>: The message to record in the log.
     * @param entry     <code>TracedEntry&lt;?&gt;</code>: The field pathway to
     *                  record.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public PropertyException(String message, TracedEntry<?> entry, String errorCode) {

        super(entry.getPathway().getPath(), "JSON parsing exception at field [" + entry + "] : " + message,
                "Property Exception -> " + errorCode);
    }

    /**
     * Checks whether or not a field is an index or a string, and names it
     * accordingly.
     * 
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The field pathway to check.
     * @return <code>String</code>: The resulting field string.
     */
    public static final String propertyName(TracedEntry<?> entry) {

        ImmutableList<Object> path = entry.getPathway().getInternalPath();

        Object key = path.isEmpty() ? null : path.getLast();

        if (key == null) {

            return "[PROPERTY UNLISTED]";
        }
        return (key instanceof Number ? "Index " + key : "Property \"" + key + "\"");
    }
}
