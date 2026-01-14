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
import com.transcendruins.utilities.metadata.Version;

/**
 * <code>VersionBoundsException</code>: An exception thrown to indicate an
 * invalid version bounds.
 */
public final class VersionBoundsException extends PropertyException {

    /**
     * Creates a new instance of the <code>VersionBoundsException</code> exception.
     * 
     * @param message <code>String</code>: The generated error message to save to
     *                the logs file.
     * @param entry   <code>TracedEntry&lt;?&gt;</code>: The field pathway to
     *                record.
     */
    private VersionBoundsException(String message, TracedEntry<?> entry) {

        super(message, entry, "Version Bounds Exception");
    }

    /**
     * Creates a new instance of the <code>VersionBoundsException</code> exception
     * which represents a set of version bounds whose maximum bounds is less than
     * its minimum bounds.
     * 
     * @param maxVersion <code>TracedEntry&lt;?&gt;</code>: The <code>Version</code>
     *                   instance representing the maximum version in the version
     *                   bounds.
     * @param minVersion <code>TracedEntry&lt;?&gt;</code>: The <code>Version</code>
     *                   instance representing the minimum version in the version
     *                   bounds.
     * @return <code>VersionBoundsException</code>: The generated
     *         <code>VersionBoundsException</code> exception.
     */
    public static VersionBoundsException inverseMaxAndMinBounds(TracedEntry<Version> maxVersion,
            TracedEntry<Version> minVersion) {

        return new VersionBoundsException("Maximum version " + maxVersion.getValue()
                + " may not be lower than minimum version " + minVersion.getValue() + ".", maxVersion);
    }
}
