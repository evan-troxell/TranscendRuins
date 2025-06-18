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

package com.transcendruins.resources.languages;

import java.util.HashMap;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>LanguageSet</code>: A class representing a set of compiled languages.
 */
public final class LanguageSet {

    /**
     * <code>ImmutableMap&lt;String, Language&gt;</code>: The set of all languages
     * in this <code>LanguageSet</code> instance.
     */
    private final ImmutableMap<String, Language> languages;

    /**
     * Retrieves the set of all languages in this <code>LanguageSet</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Language&gt;</code>: The
     *         <code>languages</code> field of this <code>LanguageSet</code>
     *         instance.
     */
    public ImmutableMap<String, Language> getLanguages() {

        return languages;
    }

    /**
     * Creates a new instance of the <code>LanguageSet</code> class.
     * 
     * @param path <code>TracedPath</code>: The path which leads to this
     *             <code>LanguageSet</code> instance.
     */
    public LanguageSet(TracedPath path) {

        HashMap<String, Language> languagesMap = new HashMap<>();

        for (TracedPath subPath : path.listRecursiveFiles(TracedPath.JSON)) {

            try {

                languagesMap.put(subPath.getFileStem(), new Language(subPath));
            } catch (LoggedException _) {
            }
        }

        languages = new ImmutableMap<>(languagesMap);
    }
}
