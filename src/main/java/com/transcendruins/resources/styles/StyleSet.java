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

package com.transcendruins.resources.styles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
     * <code>StyleSet</code>: An empty style set.
     */
    public static final StyleSet EMPTY = new StyleSet();

    /**
     * Creates a new, empty instance of the <code>StyleSet</code> class.
     */
    private StyleSet() {

        styles = new ImmutableList<>();
    }

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
    public final boolean isEmpty() {

        return styles.isEmpty();
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
     * @throws LoggedException Thrown if the collection could not be parsed
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
     * @throws LoggedException Thrown if the collection could not be parsed.
     */
    private ImmutableList<StyleCase> parseStyles(TracedDictionary json) throws LoggedException {

        ArrayList<StyleCase> stylesList = new ArrayList<>();
        for (String styleKey : json) {

            stylesList.add(new StyleCase(json, styleKey));
        }

        return new ImmutableList<>(stylesList);
    }

    /**
     * <code>StyleCase</code>: A class representing a CSS selector and attached
     * style.
     */
    private final class StyleCase {

        /**
         * <code>ImmutableList&lt;ImmutableList&lt;ComponentCombinator&gt;&gt;</code>:
         * The assorted combinator cases, listed first by options and second by the
         * specific cases required.
         */
        private final ImmutableList<ImmutableList<ComponentCombinator>> cases;

        /**
         * <code>Style</code>: The style to apply if the cases are matched.
         */
        private final Style style;

        /**
         * Creates a new instance of the <code>StyleCase</code> class.
         * 
         * @param parent <code>TracedDictionary</code>: The parent collection to use.
         * @param key    <code>String</code>: The key to parse.
         * @throws LoggedException Thrown if the combinator key was invalid or the style
         *                         collection could not be parsed.
         */
        public StyleCase(TracedDictionary parent, String key) throws LoggedException {

            style = Style.createStyle(parent, key);

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

                // Isolate all combinator indices.
                for (int i = 0; i < option.length(); i++) {

                    char c = option.charAt(i);
                    if (isCombinator(c)) {

                        indices.add(i);
                    }
                }

                ArrayList<ComponentCombinator> combinators = new ArrayList<>();

                // Isolate the final combinator.
                combinators.add(new ComponentCombinator(parent, option.substring(indices.getLast() + 1),
                        (p, s, o) -> s.matches(p) && select(p, o)));

                for (int i = indices.size() - 1; i > 0; i--) {

                    // Retrieve the combinator at the end.
                    String val = option.substring(indices.get(i - 1) + 1, indices.get(i) + 1);

                    // Strip the combinator from the end.
                    char combinator = val.charAt(val.length() - 1);
                    val = val.substring(0, val.length() - 1);

                    // If the string was a lone combinator, throw an error.
                    if (val.isEmpty()) {

                        throw new KeyNameException(parent, option);
                    }

                    combinators.add(new ComponentCombinator(parent, val, switch (combinator) {

                    // Immediate preceding sibling.
                    case '+' -> (p, s, o) -> {

                        p = p.getPrecedingSibling();
                        if (p == null) {

                            return false;
                        }
                        return s.matches(p) && select(p, o);
                    };

                    // Preceding sibling.
                    case '~' -> (p, s, o) -> {

                        do {

                            p = p.getPrecedingSibling();
                            if (p == null) {

                                return false;
                            }
                        } while (!s.matches(p) || !select(p, o));
                        return true;
                    };

                    // Direct descendant (child) combinator.
                    case '>' -> (p, s, o) -> {

                        p = p.getParent();
                        if (p == null) {

                            return false;
                        }
                        return s.matches(p) && select(p, o);
                    };

                    // Descendant combinator.
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

        /**
         * Determines whether or not a character is a combinator.
         * 
         * @param c <code>char</code>: The character to check.
         * @return <code>boolean</code>: Whether or not the character was a valid
         *         combinator.
         */
        private boolean isCombinator(char c) {

            return c == ' ' || c == '>' || c == '+' || c == '~';
        }

        /**
         * Determines whether or not a component matches this <code>StyleCase</code>
         * instance.
         * 
         * @param properties <code>ComponentProperties</code>: The component properties
         *                   to check.
         * @return <code>boolean</code>: Whether or not the component met all of the
         *         conditions in at least one case of this <code>StyleSet</code>
         *         instance.
         */
        public final boolean matches(ComponentProperties properties) {

            // Check all possible case options for a match.
            for (List<ComponentCombinator> combinators : cases) {

                if (select(properties, combinators)) {

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Checks if a component matches the last combinator in a list and propagates
     * downwards if possible.
     * 
     * @param properties  <code>ComponentProperties</code>: The component properties
     *                    to check.
     * @param combinators <code>List&lt;ComponentCombinator&gt;</code>: The
     *                    combinators to apply.
     * @return <code>boolean</code>: Whether or not the combinator list matches the
     *         component.
     */
    private static boolean select(ComponentProperties properties, List<ComponentCombinator> combinators) {

        if (combinators.isEmpty()) {

            return true;
        }

        ComponentCombinator combinator = combinators.getFirst();
        ComponentSelector selector = combinator.selector;
        CombinatorOperator operator = combinator.operator;

        combinators = combinators.subList(1, combinators.size());

        return operator.operate(properties, selector, combinators);
    }

    /**
     * <code>CombinatorOperator</code>: A functional interface representing a
     * combinator operating on a component, a selector, and a list of sequential
     * combinators.
     */
    @FunctionalInterface
    private interface CombinatorOperator {

        /**
         * Determines whether or not a component matches this
         * <code>CombinatorOperator</code> instance.
         * 
         * @param properties  <code>ComponentProperties</code>: The component properties
         *                    to check.
         * @param selector    <code>ComponentSelector</code>: The selector to match the
         *                    properties against.
         * @param combinators <code>List&lt;ComponentCombinator&gt;</code>: The
         *                    sequential operators to match.
         * @return <code>boolean</code>: Whether or not the component met all of the
         *         conditions in this <code>CombinatorOperator</code> instance and the
         *         subsequent cases.
         */
        public boolean operate(ComponentProperties properties, ComponentSelector selector,
                List<ComponentCombinator> combinators);
    }

    /**
     * <code>ComponentCombinator</code>: A class representing a combinator which
     * contains both the selection criteria and the combinator operator type.
     */
    private final class ComponentCombinator {

        /**
         * <code>ComponentSelector</code>: The selector method to apply.
         */
        private final ComponentSelector selector;

        /**
         * <code>CombinatorOperator</code>: The combinator operator to apply.
         */
        private final CombinatorOperator operator;

        /**
         * Creates a new instance of the <code>ComponentCombinator</code> class.
         * 
         * @param parent <code>TracedDictionary</code>: The parent collection to use.
         * @param key    <code>String</code>: The key to parse.
         * @throws LoggedException Thrown if the combinator key was invalid.
         */
        public ComponentCombinator(TracedDictionary parent, String key, CombinatorOperator operator)
                throws LoggedException {

            selector = new ComponentSelector(parent, key);
            this.operator = operator;
        }
    }

    /**
     * <code>ComponentSelector</code>: The selector used to match component
     * properties.
     */
    private final class ComponentSelector {

        /**
         * <code>String</code>: The component type to match.
         */
        private final String type;

        /**
         * <code>String</code>: The component id to match.
         */
        private final String id;

        /**
         * <code>ImmutableSet&lt;String&gt;</code>: The component classes to match.
         */
        private final ImmutableSet<String> classes;

        /**
         * <code>ImmutableSet&lt;String&gt;</code>: The component states (pseudoclasses)
         * to match.
         */
        private final ImmutableSet<String> states;

        /**
         * Creates a new instance of the <code>ComponentSelector</code> class.
         * 
         * @param parent <code>TracedDictionary</code>: The parent collection to use.
         * @param key    <code>String</code>: The key to parse.
         * @throws LoggedException Thrown if the combinator key was invalid.
         */
        public ComponentSelector(TracedDictionary parent, String key) throws LoggedException {

            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < key.length(); i++) {

                char c = key.charAt(i);
                if (isSeparator(c)) {

                    indices.add(i);
                }
            }

            indices.add(key.length());

            int first = indices.getFirst();
            type = (first == 0 || key.charAt(0) == '*') ? null : key.substring(0, first);

            String idString = null;
            HashSet<String> classesList = new HashSet<>();
            HashSet<String> statesList = new HashSet<>();

            for (int i = 0; i < indices.size() - 1; i++) {

                String val = key.substring(indices.get(i), indices.get(i + 1));
                char selector = val.charAt(0);
                val = val.substring(1);
                if (val.isEmpty()) {

                    throw new KeyNameException(parent, key);
                }

                switch (selector) {

                case '.' -> classesList.add(val);

                case '#' -> {

                    if (idString != null) {

                        throw new KeyNameException(parent, key);
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

        /**
         * Determines whether or not a character is a separator.
         * 
         * @param c <code>char</code>: The character to check.
         * @return <code>boolean</code>: Whether or not the character was a valid
         *         separator.
         */
        private boolean isSeparator(char c) {

            return c == '.' || c == '#' || c == ':';
        }

        /**
         * Determines whether or not a component matches this
         * <code>ComponentSelector</code> instance.
         * 
         * @param properties <code>ComponentProperties</code>: The component properties
         *                   to check.
         * @return <code>boolean</code>: Whether or not the component met all of the
         *         conditions in this <code>ComponentSelector</code> instance and the
         *         subsequent cases.
         */
        public final boolean matches(ComponentProperties properties) {

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
    public static final StyleSet createStyleSet(List<StyleSet> styleSets) {

        List<StyleCase> compiledStyles = styleSets.stream().filter(set -> !set.isEmpty())
                .flatMap(styles -> styles.styles.stream()).toList();

        return new StyleSet(compiledStyles);
    }

    /**
     * Extends this <code>StyleSet</code> instance by another.
     * 
     * @param set <code>StyleSet</code>: The style set to add at the end.
     * @return <code>StyleSet</code>: The resulting style set.
     */
    public final StyleSet extend(StyleSet set) {

        ArrayList<StyleCase> stylesList = new ArrayList<>(styles.size() + set.styles.size());
        stylesList.addAll(styles);
        stylesList.addAll(set.styles);

        return new StyleSet(stylesList);
    }

    /**
     * Retrieves a style from a stack of style sets. Styles which have higher
     * priority will be placed at the end of the list, while lesser priorities will
     * be closer to the beginning.
     * 
     * @param properties <code>ComponentProperties</code>: The component properties
     *                   to match.
     * @return <code>List&lt;Style&gt;</code>: The generated styles.
     */
    public final List<Style> getStyle(ComponentProperties properties) {

        return styles.stream().filter(styleCase -> styleCase.matches(properties)).map(styleCase -> styleCase.style)
                .toList();
    }
}
