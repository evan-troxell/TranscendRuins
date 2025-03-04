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

package com.transcendruins.assets.assets;

import java.util.ArrayList;
import java.util.HashMap;

import com.transcendruins.assets.AssetType;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedCollection.JSONType;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>AssetPresets</code>: A class representing the presets of any
 * asset type, including but not limited to: layouts, elements, entities,
 * items, and more.
 */
public abstract class AssetPresets {

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The identifier of this
     * <code>AssetPresets</code> instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * Retrieves the identifier entry of this <code>AssetPresets</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>AssetPresets</code>
     * instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the identifier of this <code>AssetPresets</code> instance.
     * 
     * @return <code>Identifier</code>: The value of the
     *         <code>identifierEntry</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>AssetType</code>: The asset type of this <code>AssetPresets</code>
     * instance.
     */
    private final AssetType type;

    /**
     * Retrieves the asset type of this <code>AssetPresets</code> instance.
     * 
     * @return <code>AssetType</code>: The <code>type</code> field of this
     *         <code>AssetPresets</code> instance.
     */
    public final AssetType getType() {

        return type;
    }

    /**
     * <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: A list of the
     * initial events of this <code>AssetPresets</code> instance.
     */
    private final ImmutableList<TracedEntry<String>> events;

    /**
     * Retrieves the initial events of this <code>AssetPresets</code> instance.
     * 
     * @return <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The
     *         <code>events</code> field of this <code>AssetPresets</code>
     *         instance.
     */
    public final ImmutableList<TracedEntry<String>> getEvents() {

        return events;
    }

    private final ImmutableMap<String, Object> publicProperties;

    public final ImmutableMap<String, Object> getPublicProperties() {

        return publicProperties;
    }

    private final ImmutableMap<String, Object> privateProperties;

    public final ImmutableMap<String, Object> getPrivateProperties() {

        return privateProperties;
    }

    /**
     * Creates a new instance of the <code>AssetPresets</code> class.
     * 
     * @param collection <code>TracedCollection</code>: The collection from which
     *                   this <code>AssetPresets</code> instance should be created.
     * @param key        <code>Object</code>: The key to retrieve from the
     *                   <code>collection</code> parameter.
     * @param type       <code>AssetType</code>: The asset type of this
     *                   <code>AssetPresets</code> instance.
     * @throws LoggedException Thrown if any exception is raised while creating this
     *                         <code>AssetPresets</code> instance.
     */
    protected AssetPresets(TracedCollection collection, Object key, AssetType type)
            throws LoggedException {

        this.type = type;
        this.identifierEntry = collection.getAsMetadata(key, false, false);
        this.identifier = identifierEntry.getValue();

        ArrayList<TracedEntry<String>> eventsList = new ArrayList<>();
        HashMap<String, Object> propertiesMap = new HashMap<>();

        if (collection.getType(key) == JSONType.DICT) {

            TracedEntry<TracedDictionary> assetEntry = collection.getAsDict(key, false);
            TracedDictionary json = assetEntry.getValue();

            TracedEntry<TracedArray> eventsEntry = json.getAsArray("events", true);
            if (eventsEntry.containsValue()) {

                TracedArray eventsJson = eventsEntry.getValue();

                for (int i : eventsJson.getIndices()) {

                    eventsList.add(eventsJson.getAsString(i, false, null));
                }
            }

            TracedEntry<TracedDictionary> propertiesEntry = json.getAsDict("properties", true);
            if (propertiesEntry.containsValue()) {

                TracedDictionary propertiesJson = propertiesEntry.getValue();
                for (String property : propertiesJson.getKeys()) {

                    propertiesMap.put(property, propertiesJson.getAsScalar(property, true, null));
                }
            }
        }

        events = new ImmutableList<>(eventsList);
        publicProperties = new ImmutableMap<>(propertiesMap);

        privateProperties = new ImmutableMap<>();
    }
}
