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

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>FileException</code>: A general exception thrown to indicate an invalid
 * file. This is a general exception, and should be inherited upon to improve
 * clarity.
 */
public class FileException extends LoggedException {

    /**
     * Creates a new instance of the <code>FileException</code> exception.
     * 
     * @param message   <code>String</code>: The message to record in the log.
     * @param errorCode <code>String</code>: The error type to record in the log.
     */
    public FileException(TracedPath path, String message, String errorCode) {

        super(path, message, "File Exception -> " + errorCode);
    }

}
