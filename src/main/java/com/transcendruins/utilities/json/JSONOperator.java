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

package com.transcendruins.utilities.json;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.transcendruins.utilities.exceptions.fileexceptions.FileFormatException;
import com.transcendruins.utilities.exceptions.fileexceptions.MissingPathException;
import com.transcendruins.utilities.files.ExternalPath;
import com.transcendruins.utilities.files.TracedPath;

/**
 * <code>JSONOperator</code>: A set of operation methods to parse and process
 * JSON information.
 */
public final class JSONOperator {

    private static final JSONParser parser = new JSONParser();

    private static final ContainerFactory orderedFactory = new ContainerFactory() {
        @SuppressWarnings("rawtypes")
        @Override
        public final Map createObjectContainer() {
            return new LinkedHashMap<>(); // preserves order
        }

        @SuppressWarnings("rawtypes")
        @Override
        public final List creatArrayContainer() {
            return new LinkedList<>(); // order already preserved in lists
        }
    };

    /**
     * Parses a JSON string into a <code>JSONObject</code>.
     * 
     * @param jsonString <code>String</code>: The string to parse.
     * @return <code>Object</code>: The resulting object.
     * @throws ParseException Thrown when the JSON string is in an invalid format.
     */
    public static Object parseJSON(String jsonString) throws ParseException {

        return parser.parse(jsonString, orderedFactory);
    }

    /**
     * Retrieves the contents of a file and parses its contents into a
     * <code>JSONObject</code> formatted as a <code>TracedDictionary</code>.
     * 
     * @param path <code>TracedPath</code>: The path to search for.
     * @return <code>TracedDictionary</code>: The resulting <code>JSONObject</code>
     *         formatted as a <code>TracedDictionary</code>.
     * @throws FileFormatException  Thrown when the file is in an invalid format.
     * @throws MissingPathException Thrown when the file is missing or otherwise
     *                              cannot be read.
     */
    public static TracedDictionary retrieveJSON(TracedPath path) throws FileFormatException, MissingPathException {

        String jsonString = path.retrieve();

        // Raise an error if the string could not be retrieved.
        if (jsonString == null) {

            throw new MissingPathException(path, false);
        }
        try {

            if (parseJSON(jsonString) instanceof Map json) {

                return new TracedDictionary(json, path);
            }

            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
        } catch (ParseException e) {

            throw new FileFormatException(path);
        }
    }

    /**
     * Retrieves the contents of a file and parses its contents into a
     * <code>JSONArray</code> formatted as a <code>TracedArray</code>.
     * 
     * @param path <code>TracedPath</code>: The path to search for.
     * @return <code>TracedArray</code>: The resulting <code>JSONArray</code>
     *         formatted as a <code>TracedArray</code>.
     * @throws FileFormatException  Thrown when the file is in an invalid format.
     * @throws MissingPathException Thrown when the file is missing or otherwise
     *                              cannot be read.
     */
    public static TracedArray retrieveJSONArray(TracedPath path) throws FileFormatException, MissingPathException {

        String jsonString = path.retrieve();

        // Raise an error if the string could not be retrieved.
        if (jsonString == null) {

            throw new MissingPathException(path, false);
        }
        try {

            if (parseJSON(jsonString) instanceof List json) {

                return new TracedArray(json, path);
            }

            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
        } catch (ParseException e) {

            throw new FileFormatException(path);
        }
    }

    /**
     * Writes the JSON information from a <code>JSONObject</code> to the designated
     * file from an <code>ExternalPath</code> directory.
     * 
     * @param path <code>ExternalPath</code>: The path to write to.
     * @param json <code>JSONObject</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for
     *                     any reason.
     */
    public static void writeTo(ExternalPath path, JSONObject json) throws IOException {

        String outputString = json.toJSONString();
        path.writeTo(outputString);
    }

    /**
     * Prevents the <code>JSONOperator</code> class from being instantiated.
     */
    private JSONOperator() {
    }
}
