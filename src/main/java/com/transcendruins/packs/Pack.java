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

package com.transcendruins.packs;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.fileexceptions.MissingPathException;
import com.transcendruins.utilities.exceptions.propertyexceptions.CollectionSizeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.StringLengthException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>Pack</code>: A class representing the module information of an asset
 * pack or resource pack.
 */
public abstract class Pack {

    /**
     * <code>TracedPath</code>: The directory of the root folder of this
     * <code>Pack</code> instance.
     */
    private final TracedPath root;

    /**
     * Retrieves the root of this <code>Pack</code> instance.
     * 
     * @return <code>TracedPath</code>: The <code>root</code> field of this
     *         <code>Pack</code> instance.
     */
    public final TracedPath getRoot() {

        return root;
    }

    /**
     * <code>TracedDictionary</code>: The JSON information of this <code>Pack</code>
     * instance.
     */
    private final TracedDictionary json;

    /**
     * Retrieves the JSON information of this <code>Pack</code> instance.
     * 
     * @return <code>TracedDictionary</code>: The <code>json</code> field of this
     *         <code>Pack</code> instance.
     */
    public final TracedDictionary getJson() {

        return json;
    }

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The identifier entry of this
     * <code>PackSchema</code> instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * Retrieves the identifier entry of this <code>PackSchema</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this <code>PackSchema</code>
     *         instance.
     */
    public final TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>Pack</code> instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the identifier of this <code>Pack</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>identifier</code> field of this
     *         <code>Pack</code> instance.
     */
    public final Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>String</code>: The name of this <code>Pack</code> instance.
     */
    private final String name;

    /**
     * Retrieves the name of this <code>Pack</code> instance.
     * 
     * @return <code>String</code>: The <code>name</code> field of this
     *         <code>Pack</code> instance.
     */
    public final String getName() {

        return name;
    }

    /**
     * <code>String</code>: The description of this <code>Pack</code> instance.
     */
    private final String description;

    /**
     * Retrieves the description of this <code>Pack</code> instance.
     * 
     * @return <code>String</code>: The <code>description</code> field of this
     *         <code>Pack</code> instance.
     */
    public final String getDescription() {

        return description;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The authors of this
     * <code>Pack</code> instance.
     */
    private final ImmutableList<String> authors;

    /**
     * Retrieves the authors of this <code>Pack</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>authors</code>
     *         field of this <code>Pack</code> instance.
     */
    public final ImmutableList<String> getAuthors() {

        return authors;
    }

    /**
     * <code>ImageIcon</code>: The display icon of this <code>Pack</code> instance.
     */
    private final ImageIcon displayIcon;

    /**
     * Retrieves the display icon of this <code>Pack</code> instance.
     * 
     * @return <code>ImageIcon</code>: The <code>displayIcon</code> field of this
     *         <code>Pack</code> instance.
     */
    public final ImageIcon getDisplayIcon() {

        return displayIcon;
    }

    /**
     * Creates a new instance of the <code>Pack</code> class.
     * 
     * @param root <code>TracedPath</code>: The pathway to this <code>Pack</code>
     *             instance.
     * @throws LoggedException Thrown if any exception is raised while compiling
     *                         this <code>Pack</code> instance.
     */
    public Pack(TracedPath root) throws LoggedException {

        this.root = root;

        TracedPath manifestPath = root.extend("manifest.json");

        json = JSONOperator.retrieveJSON(manifestPath);
        identifierEntry = json.getAsMetadata("metadata", false, true);
        identifier = identifierEntry.getValue();

        TracedEntry<TracedDictionary> metadataEntry = json.getAsDict("metadata", false);
        TracedDictionary metadataJson = metadataEntry.getValue();

        TracedEntry<String> nameEntry = metadataJson.getAsString("name", false, null);
        name = nameEntry.getValue();
        if (name.isEmpty()) {

            throw new StringLengthException(nameEntry);
        }

        TracedEntry<String> descriptionEntry = metadataJson.getAsString("description", true, "[DESCRIPTION UNLISTED]");
        description = descriptionEntry.getValue();

        ArrayList<String> authorsList = new ArrayList<>();

        TracedEntry<String> authorEntry = metadataJson.getAsString("author", true, null);
        if (authorEntry.containsValue()) {

            String author = authorEntry.getValue();

            if (author.isEmpty()) {

                throw new StringLengthException(authorEntry);
            }
            authorsList.add(author);

        }

        TracedEntry<TracedArray> authorsEntry = metadataJson.getAsArray("authors", true);
        if (authorsEntry.containsValue()) {

            TracedArray authorsJson = authorsEntry.getValue();
            if (authorsJson.isEmpty()) {

                throw new CollectionSizeException(authorsEntry, authorsJson);
            }

            for (int i : authorsJson) {

                TracedEntry<String> newAuthorEntry = authorsJson.getAsString(i, false, null);
                String author = newAuthorEntry.getValue();

                if (author.isEmpty()) {

                    throw new StringLengthException(newAuthorEntry);
                }
                authorsList.add(author);
            }
        }

        authors = new ImmutableList<>(authorsList);

        TracedEntry<String> displayIconEntry = metadataJson.getAsString("displayIconPath", true, "displayIcon.png");
        TracedPath displayIconPath = root.extend(displayIconEntry.getValue());
        displayIcon = displayIconPath.retrieveImage();

        if (displayIcon == null) {

            throw new MissingPathException(displayIconPath, false);
        }
    }

    /**
     * Creates a new instance of the <code>Pack</code> from a schema module.
     * 
     * @param schema <code>Pack</code>: The schema template to inherit from.
     */
    public Pack(Pack schema) {

        root = schema.getRoot();
        json = schema.getJson();

        identifierEntry = schema.getIdentifierEntry();
        identifier = schema.getIdentifier();

        name = schema.getName();
        description = schema.getDescription();
        authors = schema.getAuthors();
        displayIcon = schema.getDisplayIcon();
    }

    /**
     * Returns the string representation of this <code>Pack</code> instance.
     * 
     * @return <code>String</code>: This <code>Pack</code> instance in the following
     *         representation: <br>
     *         "<code>namespace:identifier [a, b, c]</code>"
     */
    @Override
    public final String toString() {

        return getIdentifier().toString();
    }
}
