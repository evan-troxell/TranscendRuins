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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.KeyNameException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>StyleSet</code>: A class representing a set of compiled styles.
 */
public final class StyleSet {

    /**
     * <code>ImmutableList&lt;StyleCase&gt;</code>: The list of all styles in this
     * <code>StyleSet</code> instance.
     */
    private final ImmutableList<StyleCase> styles;

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

        styles = new ImmutableList<>();
    }

    /**
     * Creates a new instance of the <code>StyleSet</code> class.
     * 
     * @param path <code>TracedPath</code>: The path which leads to this
     *             <code>StyleSet</code> instance.
     */
    public StyleSet(TracedPath path) {

        ImmutableList<StyleCase> stylesList = new ImmutableList<>();

        if (path.exists()) {

            try {

                TracedDictionary json = JSONOperator.retrieveJSON(path);
                stylesList = parseStyles(json);
            } catch (LoggedException _) {
            }
        }

        styles = stylesList;
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
        styles = entry.containsValue() ? parseStyles(entry.getValue()) : new ImmutableList<>();
    }

    /**
     * Parses a set of styles from a JSON dictionary.
     * 
     * @param json <code>TracedDictionary</code>: The JSON information to parse.
     * @return <code>ImmutableList&lt;StyleCase&gt;</code>: The resulting style set.
     */
    private ImmutableList<StyleCase> parseStyles(TracedDictionary json) throws LoggedException {

        ArrayList<StyleCase> stylesList = new ArrayList<>();
        for (String styleKey : json) {

            stylesList.add(new StyleCase(json, styleKey));
        }

        return new ImmutableList<>(stylesList);
    }

    private final class StyleCase {

        private final ImmutableList<ImmutableList<ComponentCombinator>> cases;

        private final Style style;

        public StyleCase(TracedDictionary collection, String key) throws LoggedException {

            style = Style.createStyle(collection, key);

            ArrayList<ImmutableList<ComponentCombinator>> casesList = new ArrayList<>();

            String[] options = key.split(",");
            for (String option : options) {

                // Strip leading or trailing whitespace.
                option = option.trim();

                // Strip combinators of surrounding whitespace.
                option = option.replaceAll("\\s*([>+~])\\s*", "$1");

                // Collapse remaining whitespace.
                option = option.replaceAll("\\s+", " ");

                ArrayList<Integer> indices = new ArrayList<>();
                indices.add(-1);

                for (int i = 0; i < option.length(); i++) {

                    char c = option.charAt(i);
                    if (isCombinator(c)) {

                        indices.add(i);
                    }
                }

                ArrayList<ComponentCombinator> combinators = new ArrayList<>();

                combinators.add(new ComponentCombinator(collection, option.substring(indices.getLast() + 1),
                        (p, s, o) -> s.matches(p) && select(p, o)));

                for (int i = indices.size() - 1; i > 0; i--) {

                    String val = option.substring(indices.get(i - 1) + 1, indices.get(i) + 1);
                    char combinator = val.charAt(0);
                    val = val.substring(1);
                    if (val.isEmpty()) {

                        throw new KeyNameException(collection, option);
                    }

                    combinators.add(new ComponentCombinator(collection, val, switch (combinator) {

                    case '+' -> (p, s, o) -> {
                        p = p.getPrecedingSibling();
                        return s.matches(p) && select(p, o);
                    };

                    case '~' -> (p, s, o) -> {
                        do {

                            p = p.getPrecedingSibling();
                            if (p == null) {

                                return false;
                            }
                        } while (!s.matches(p) || !select(p, o));
                        return true;
                    };

                    case '>' -> (p, s, o) -> {

                        p = p.getParent();
                        return s.matches(p) && select(p, o);
                    };

                    default -> (p, s, o) -> {

                        do {
                            p = p.getParent();
                            if (p == null) {

                                return false;
                            }
                        } while (!s.matches(p));
                        return select(p, o);
                    };
                    }));
                }

                casesList.add(new ImmutableList<>(combinators));
            }

            cases = new ImmutableList<>(casesList);
        }

        private boolean isCombinator(char c) {

            return c == ' ' || c == '>' || c == '+' || c == '~';
        }

        public boolean matches(ComponentProperties properties) {

            // Check all possible case options for a match.
            for (List<ComponentCombinator> combinators : cases) {

                if (select(properties, combinators)) {

                    return true;
                }
            }

            return false;
        }
    }

    private boolean select(ComponentProperties properties, List<ComponentCombinator> operators) {

        if (operators.isEmpty()) {

            return true;
        }

        ComponentCombinator combinator = operators.getFirst();
        ComponentSelector selector = combinator.selector;
        CombinatorOperator operator = combinator.operator;

        operators = operators.subList(1, operators.size());

        return operator.operate(properties, selector, operators);
    }

    @FunctionalInterface
    private interface CombinatorOperator {

        public boolean operate(ComponentProperties properties, ComponentSelector selector,
                List<ComponentCombinator> operators);
    }

    private final class ComponentCombinator {

        private final ComponentSelector selector;

        private final CombinatorOperator operator;

        public ComponentCombinator(TracedDictionary collection, String key, CombinatorOperator operator)
                throws LoggedException {

            selector = new ComponentSelector(collection, key);
            this.operator = operator;
        }
    }

    private final class ComponentSelector {

        private final String type;

        private final String id;

        private final ImmutableSet<String> classes;

        private final ImmutableSet<String> states;

        public ComponentSelector(TracedDictionary collection, String key) throws LoggedException {

            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < key.length(); i++) {

                char c = key.charAt(i);
                if (isSeparator(c)) {

                    indices.add(i);
                }
            }

            indices.add(key.length());

            int first = indices.getFirst();
            type = first == 0 || key.charAt(0) == '*' ? null : key.substring(0, first);

            String idString = null;
            HashSet<String> classesList = new HashSet<>();
            HashSet<String> statesList = new HashSet<>();

            for (int i = 0; i < indices.size() - 1; i++) {

                String val = key.substring(indices.get(i), indices.get(i + 1));
                char selector = val.charAt(0);
                val = val.substring(1);
                if (val.isEmpty()) {

                    throw new KeyNameException(collection, key);
                }

                switch (selector) {

                case '.' -> classesList.add(val);

                case '#' -> {

                    if (idString != null) {

                        throw new KeyNameException(collection, key);
                    }

                    idString = val;
                }

                case ':' -> statesList.add(val);
                }
            }

            id = idString;
            classes = new ImmutableSet<>(classesList);
            states = new ImmutableSet<>(statesList);
        }

        private boolean isSeparator(char c) {

            return c == '.' || c == '#' || c == ':';
        }

        public boolean matches(ComponentProperties properties) {

            if (type != null && !type.equals(properties.getType())) {

                return false;
            }

            if (id != null && !id.equals(properties.getId())) {

                return false;
            }

            if (!properties.getClasses().containsAll(classes)) {

                return false;
            }

            return properties.getStates().containsAll(states);
        }
    }

    /**
     * Creates a new instance of the <code>StyleSet</code> class.
     * 
     * @param stylesList <code>List&lt;StyleCase&gt;</code>: The map of styles to
     *                   assign to this <code>StyleSet</code> instance.
     */
    private StyleSet(List<StyleCase> stylesList) {

        styles = new ImmutableList<>(stylesList);
    }

    /**
     * Compiles an ordered list of styles into a new <code>StyleSet</code> instance.
     * 
     * @param styleSets <code>List&lt;StyleSet&gt;</code>: The styles to process.
     * @return <code>StyleSet</code>: The resulting style.
     */
    public static StyleSet createStyleSet(List<StyleSet> styleSets) {

        List<StyleCase> compiledStyles = styleSets.stream().flatMap(styles -> styles.styles.stream()).toList();

        return new StyleSet(compiledStyles);
    }

    public StyleSet extend(StyleSet set) {

        ArrayList<StyleCase> stylesList = new ArrayList<>(styles.size() + set.styles.size());
        stylesList.addAll(styles);
        stylesList.addAll(set.styles);

        return new StyleSet(stylesList);
    }

    /**
     * Retrieves a style from a stack of style sets.
     * 
     * @param properties <code>ComponentProperties</code>: The component properties
     *                   to match.
     * @return <code>List&lt;Style&gt;</code>: The generated styles.
     */
    public List<Style> getStyle(ComponentProperties properties) {

        return styles.stream().filter(styleCase -> styleCase.matches(properties)).map(styleCase -> styleCase.style)
                .toList();
    }
}
