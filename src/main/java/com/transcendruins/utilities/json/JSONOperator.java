package com.transcendruins.utilities.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.transcendruins.utilities.exceptions.fileexceptions.FileFormatException;
import com.transcendruins.utilities.exceptions.fileexceptions.MissingFileException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.files.TracedPath;

/**
 * A set of operation methods to parse and process JSON information.
 */
public final class JSONOperator {

    /**
     * Parses a JSON string into a <code>JSONObject</code>.
     * @param jsonString <code>String</code>: The string to parse.
     * @return <code>JSONObject</code>: The resulting JSON object.
     * @throws ParseException Thrown when the JSON string is in an invalid format.
     */
    public static JSONObject parseJSON(String jsonString) throws ParseException {

        Object val = new JSONParser().parse(jsonString);
        if (val instanceof JSONObject jSONObject) {

            return jSONObject;
        }
        throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
    }

    /**
     * Retrieves the contents of a file and parses its contents into a <code>JSONObject</code> formatted as a <code>TracedDictionary</code>.
     * @param path <code>TracedPath</code>: The path to search for.
     * @return <code>TracedDictionary</code>: The resulting <code>JSONObject</code> formatted as a <code>TracedDictionary</code>.
     * @throws FileFormatException Thrown when the file is in an invalid format.
     * @throws MissingFileException Thrown when the file is missing or otherwise cannot be read.
     */
    public static TracedDictionary retrieveJSON(TracedPath path) throws FileFormatException, MissingFileException {

        String jsonString = path.retrieveContents();

        // Raise an error if the string could not be retrieved.
        if (jsonString == null) {

            throw new MissingFileException(path);
        }
        try {

            JSONObject parsedVal = parseJSON(jsonString);

            return new TracedDictionary((JSONObject) parsedVal, path);
        } catch (ParseException e) {

            throw new FileFormatException(path);
        }
    }

    /**
     * Compares an entry against a list of classes, and raises an exception if it is not an instance of any of them.
     * (Optionally) returns whether or not the object is NOT null - an exception will be thrown if the <code>nullCaseAllowed</code> perameter is set to false.
     * @param entry <code>TracedEntry&lt;?&gt;</code>: The entry to compare against the list of classes.
     * @param nullCaseAllowed <code>boolean</code>: Whether or not a null case will be permitted, or if an exception should be thrown.
     * @param classes <code>Class...</code>: A list of classes which the input value should be checked against.
     * @return <code>boolean</code>: Whether or not the input value is NOT null (note that this will always return true if nullCaseAllowed is <code>false</code>, as otherwise an exception will be thrown).
     * @throws MissingPropertyException Thrown if the parameter <code>nullCaseAllowed</code> is set to false and the input value is null.
     * @throws PropertyTypeException Thrown if the input value is not an instance of any of the classes in the perameter <code>classes</code>.
     */
    public static boolean isRequiredClass(TracedEntry<?> entry, boolean nullCaseAllowed, Class<?>... classes) throws MissingPropertyException, PropertyTypeException {

        Object obj = entry.getValue();

        if (obj == null) {

            // If a null case is allowed and the value is null, return as such.
            if (nullCaseAllowed) {

                return false;
            }
            throw new MissingPropertyException(entry);
        }

        if (!isExpectedClass(obj, classes)) {

            throw new PropertyTypeException(entry);
        }
        return true;
    }

    /**
     * Returns whether or not an object is an instance of any of a list of classes.
     * @param obj <code>Object</code>: The object to compare against the list of classes.
     * @param classes <code>Class...</code>: A list of classes which the input value should be checked against.
     * @return <code>boolean</code>: Whether or not the input value is an instance of any class in the perameter <code>classes</code>.
     */
    public static boolean isExpectedClass(Object obj, Class<?>... classes) {

        for (Class<?> c : classes) {

            if (c.isInstance(obj)) {

                return true;
            }
        }
        return false;
    }

    /**
     * Converts a <code>Map&ltString, Object&gt;</code> instance to a <code>JSONObject</code> instance.
     * @param map <code>Map&ltString, Object&gt;</code>: The map to process.
     * @return <code>JSONObject</code>: The generated JSON Object.
     */
    public static JSONObject toJSONObject(Map<String, ?> map) {

        HashMap<String, Object> recursionMap = new HashMap<>();

        for (String key : map.keySet()) {

            Object value = map.get(key);

            if (value instanceof Map<?, ?> map1) {

                // Safely convert the new value to a perameterized map.
                HashMap<String, Object> valueMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : map1.entrySet()) {

                    valueMap.put((String) entry.getKey(), entry.getValue());
                }

                recursionMap.put(key, toJSONObject(valueMap));
            } else if (value instanceof Collection) {

                // Safely convert the new value to a perameterized map.
                ArrayList<Object> valueList = new ArrayList<>();
                for (Object entry : (Collection<?>) value) {

                    valueList.add(entry);
                }

                recursionMap.put(key, toJSONArray(valueList));
            } else if (value instanceof Object[] objects) {

                recursionMap.put(key, toJSONArray(Arrays.asList(objects)));
            } else {

                recursionMap.put(key, value);
            }
        }

        return new JSONObject(recursionMap);
    }

    /**
     * Converts a <code>Collection&lt;Object&gt;</code> instance to a <code>JSONArray</code> instance.
     * @param map <code>Collection&lt;Object&gt;</code>: The collection to process.
     * @return <code>JSONArray</code>: The generated JSON Array.
     */
    @SuppressWarnings("unchecked")
    public static JSONArray toJSONArray(Collection<?> list) {

        JSONArray recursionList = new JSONArray();

        for (Object value : list) {

            if (value instanceof Map<?, ?> map) {

                // Safely convert the new value to a perameterized map.
                HashMap<String, Object> valueMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {

                    valueMap.put((String) entry.getKey(), entry.getValue());
                }

                recursionList.add(toJSONObject(valueMap));
            } else if (value instanceof Collection) {

                // Safely convert the new value to a perameterized map.
                ArrayList<Object> valueList = new ArrayList<>();
                for (Object entry : (Collection<?>) value) {

                    valueList.add(entry);
                }

                recursionList.add(toJSONArray(valueList));
            } else if (value instanceof Object[] objects) {

                recursionList.add(toJSONArray(Arrays.asList(objects)));
            } else {

                recursionList.add(value);
            }
        }
        return recursionList;
    }

    /**
     * Writes the JSON information from a <code>JSONObject</code> to the designated file from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The path to write to.
     * @param json <code>JSONObject</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for any reason.
     */
    public static void writeTo(TracedPath path, JSONObject json) throws IOException {

        String outputString = json.toJSONString();
        path.writeTo(outputString);
    }

    /**
     * Writes the JSON information from a <code>Map</code> to the designated file from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The path to write to.
     * @param values <code>Map&lt:String, ?&gt;</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for any reason.
     */
    public static void writeTo(TracedPath path, Map<String, ?> values) throws IOException {

        JSONObject json = toJSONObject(values);

        String outputString = json.toJSONString();
        path.writeTo(outputString);
    }

    /**
     * Prevents the <code>JSONOperator</code> class from being instantiated.
     */
    private JSONOperator() {}
}
