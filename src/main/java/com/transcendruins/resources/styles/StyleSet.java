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

package com.transcendruins.resources.styles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>StyleSet</code>: A class representing a set of compiled styles.
 */
public final class StyleSet {

    /**
     * <code>ImmutableMap&lt;String, Style&gt;</code>: The set of all styles in this
     * <code>StyleSet</code> instance.
     */
    private final ImmutableMap<String, Style> styles;

    /**
     * Retrieves the set of all styles in this <code>Style</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Style&gt;</code>: The
     *         <code>styles</code> field of this <code>StyleSet</code> instance.
     */
    public ImmutableMap<String, Style> getStyles() {

        return styles;
    }

    /**
     * Determines whether or not this <code>StyleSet</code> instance contains any
     * styles.
     * 
     * @return <code>boolean</code>: Whether or not the <code>styles</code> field of
     *         this <code>StyleSet</code> instance is empty.
     */
    public boolean isEmpty() {

        return styles.isEmpty();
    }

    /**
     * Creates a new, empty instance of the <code>StyleSet</code> class.
     */
    public StyleSet() {

        styles = new ImmutableMap<>();
    }

    /**
     * Creates a new instance of the <code>StyleSet</code> class.
     * 
     * @param path <code>TracedPath</code>: The path which leads to this
     *             <code>StyleSet</code> instance.
     */
    public StyleSet(TracedPath path) {

        ImmutableMap<String, Style> stylesMap = new ImmutableMap<>();

        if (path.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(path);
                stylesMap = parseStyles(json);
            } catch (LoggedException _) {
            }
        }

        styles = stylesMap;
    }

    /**
     * Creates a new instance of the <code>StyleSet</code> class.
     * 
     * @param collection <<code>TracedCollection</code>: The collection from which
     *                   this <code>StyleSet</code> instance should be created.
     * @param key        <code>Object</code>: The key to retrieve from the
     *                   <code>collection</code> parameter.
     */
    public StyleSet(TracedCollection collection, Object key) throws LoggedException {

        TracedEntry<TracedDictionary> entry = collection.getAsDict(key, true);
        styles = entry.containsValue() ? parseStyles(entry.getValue()) : new ImmutableMap<>();
    }

    /**
     * Parses a set of styles from a JSON dictionary.
     * 
     * @param json <code>TracedDictionary</code>: The JSON information to parse.
     * @return <code>ImmutableMap&lt;String, Style&gt;</code>: The resulting style
     *         set.
     */
    private ImmutableMap<String, Style> parseStyles(TracedDictionary json) throws LoggedException {

        HashMap<String, Style> stylesMap = new HashMap<>();
        for (String styleKey : json) {

            TracedEntry<TracedDictionary> styleEntry = json.getAsDict(styleKey, false);
            TracedDictionary styleJson = styleEntry.getValue();

            Style style = new Style(styleJson);
            stylesMap.put(styleKey, style);
        }

        return new ImmutableMap<>(stylesMap);
    }

    /**
     * Creates a new instance of the <code>StyleSet</code> class.
     * 
     * @param stylesMap <code>Map&lt;String, Style&gt;</code>: The map of styles to
     *                  assign to this <code>StyleSet</code> instance.
     */
    private StyleSet(Map<String, Style> stylesMap) {

        styles = new ImmutableMap<>(stylesMap);
    }

    /**
     * Compiles an ordered list of styles into a new <code>StyleSet</code> instance.
     * 
     * @param styleSets <code>List&lt;StyleSet&gt;</code>: The styles to process.
     * @return <code>StyleSet</code>: The resulting style.
     */
    public static StyleSet compileStyles(List<StyleSet> styleSets) {

        HashMap<String, Style> compiledStyles = styleSets.stream().flatMap(styles -> styles.styles.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement,
                        HashMap::new));

        return new StyleSet(compiledStyles);
    }

    /**
     * Applies another set of styles onto this <code>StyleSet</code> instance.
     * 
     * @param set <code>StyleSet</code>: The styles to apply.
     * @return <code>StyleSet</code>: The resulting style. If this
     *         <code>StyleSet</code> instance is empty, the output will be the input
     *         set. However, in the <code>set</code> parameter is empty, the output
     *         will be this <code>StyleSet</code> instance.
     */
    public StyleSet apply(StyleSet set) {

        if (isEmpty()) {

            return set;
        }

        if (set.isEmpty()) {

            return this;
        }

        HashMap<String, Style> newSet = new HashMap<>(styles);
        newSet.putAll(set.getStyles());

        return new StyleSet(newSet);
    }

    /**
     * Retrieves a style from a stack of style sets.
     * 
     * @param style <code>String</code>: The name of the style to retrieve.
     * @param sets  <code>StyleSet[]</code>: The sets to retrieve from.
     */
    public static Style getStyle(String style, StyleSet[] sets) {

        for (StyleSet set : sets) {

            if (set.styles.containsKey(style)) {

                return set.styles.get(style);
            }
        }

        return null;
    }
}
