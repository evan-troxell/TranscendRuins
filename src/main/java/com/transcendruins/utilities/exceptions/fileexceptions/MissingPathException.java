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

package com.transcendruins.utilities.exceptions.fileexceptions;

import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>MissingPathException</code>: An exception thrown to indicate that a
 * required path is missing.
 */
public final class MissingPathException extends FileException {

    /**
     * Creates a new instance of the <code>MissingPathException</code> exception.
     * 
     * @param path        <code>TracedPath</code>: The missing filepath to log.
     * @param isDirectory <code>boolean</code>: Whether or not the path to check for
     *                    is a directory.
     */
    public MissingPathException(TracedPath path, boolean isDirectory) {

        super(path, "Required %s \"%s\" is missing.".formatted(isDirectory ? "directory" : "file", path),
                "Missing %s Exception".formatted(isDirectory ? "Directory" : "File"));
    }

}
