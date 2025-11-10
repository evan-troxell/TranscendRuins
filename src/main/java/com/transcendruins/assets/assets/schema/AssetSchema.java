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

package com.transcendruins.assets.assets.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetEvent;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.ReferenceWithoutDefinitionException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;

/**
 * <code>AssetSchema</code>: A class representing any asset schema type,
 * including but not limited to: layouts, elements, entities, items, and more.
 */
public final class AssetSchema {

    /**
     * <code>AssetType</code>: The type of this <code>AssetSchema</code> instance.
     */
    private final AssetType type;

    /**
     * Retrieves the type of this <code>AssetSchema</code> instance.
     * 
     * @return <code>AssetType</code>: The <code>type</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public AssetType getType() {

        return type;
    }

    /**
     * <code>TracedEntry&lt;Identifier&gt;</code>: The identifier of this
     * <code>AssetSchema</code> instance.
     */
    private final TracedEntry<Identifier> identifierEntry;

    /**
     * Retrieves the identifier entry of this <code>AssetSchema</code> instance.
     * 
     * @return <code>TracedEntry&lt;Identifier&gt;</code>: The
     *         <code>identifierEntry</code> field of this <code>AssetSchema</code>
     *         instance.
     */
    public TracedEntry<Identifier> getIdentifierEntry() {

        return identifierEntry;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>AssetSchema</code>
     * instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the identifier of this <code>AssetSchema</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>identifier</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>AssetAttributes</code>: The base attribute set of this
     * <code>AssetSchema</code> instance.
     */
    private final AssetAttributes attributes;

    /**
     * Retrieves the base attribute set of this <code>AssetSchema</code> instance.
     * 
     * @return <code>AssetAttributes</code>: The <code>attributes</code> field of
     *         this <code>AssetSchema</code> instance.
     */
    public AssetAttributes calculateAttributes() {

        return attributes;
    }

    /**
     * <code>ImmutableMap&lt;String, AssetAttributes&gt;</code>: The permutations of
     * this <code>AssetSchema</code> instance.
     */
    private final ImmutableMap<String, AssetAttributes> permutations;

    /**
     * Retrieves a permutation from this <code>AssetSchema</code> instance.
     * 
     * @param permutation <code>String</code>: The permutation to retrieve.
     * @return <code>AssetAttributes</code>: The retrieved permutation of this
     *         <code>AssetSchema</code> instance.
     */
    public AssetAttributes getPermutation(String permutation) {

        return permutations.get(permutation);
    }

    /**
     * <code>ImmutableMap&lt;String, ImmutableList&lt;AssetEvent&gt;&gt;</code>: The
     * events of this <code>AssetSchema</code> instance.
     */
    private final ImmutableMap<String, ImmutableList<AssetEvent>> events;

    /**
     * Determines whether or not this <code>AssetSchema</code> instance contains an
     * event.
     * 
     * @param event <code>String</code>: The event to check for.
     * @return <code>boolean</code>: If the <code>events</code> field of this
     *         <code>AssetSchema</code> contains the event.
     */
    public boolean containsEvent(String event) {

        return events.containsKey(event);
    }

    /**
     * Retrieves an event from this <code>AssetSchema</code> instance.
     * 
     * @param event <code>String</code>: The event to retrieve.
     * @return <code>ImmutableList&lt;AssetEvent&gt;</code>: The retrieved event of
     *         this <code>AssetSchema</code> instance.
     */
    public ImmutableList<AssetEvent> getEvent(String event) {

        return events.get(event);
    }

    /**
     * <code>ImmutableMap&lt;String, Object&gt;</code>: The initial properties of
     * this <code>AssetSchema</code> instance.
     */
    private final ImmutableMap<String, Object> properties;

    /**
     * Retrieves the initial properties of this <code>AssetSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;String, Object&gt;</code>: The
     *         <code>properties</code> field of this <code>AssetSchema</code>
     *         instance.
     */
    public ImmutableMap<String, Object> getProperties() {

        return properties;
    }

    /**
     * <code>HashMap&lt;AssetType, HashSet&lt;TracedEntry&lt;Identifier&gt;&gt;&gt;</code>:
     * The collection of asset dependencies in this <code>AssetSchema</code>
     * instance.
     */
    private final HashMap<AssetType, HashSet<TracedEntry<Identifier>>> assetDependencies = new HashMap<>();

    /**
     * Adds an asset dependency to the map of dependencies present in this
     * <code>AssetSchema</code> instance.
     * 
     * @param dependency <code>AssetPresets</code>: The dependency presets to be
     *                   added.
     */
    protected final void addAssetDependency(AssetPresets dependency) {

        addAssetDependency(dependency.getType(), dependency.getIdentifierEntry());
    }

    /**
     * Adds an asset dependency to the map of dependencies present in this
     * <code>AssetSchema</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of the dependency to be
     *                   added.
     * @param identifier <code>TracedEntry&lt;Identifier&gt;</code>: The identifier
     *                   of the dependency to be added.
     */
    protected final void addAssetDependency(AssetType type, TracedEntry<Identifier> identifier) {

        assetDependencies.computeIfAbsent(type, _ -> new HashSet<>()).add(identifier);
    }

    /**
     * Retrieves he collection of asset dependencies in this
     * <code>AssetSchema</code> instance.
     * 
     * @return <code>ImmutableMap&lt;AssetType, ImmutableSet&lt;TracedEntry&lt;Identifier&gt;&gt;&gt;</code>:
     *         An immutable copy of the <code>assetDependencies</code> field of this
     *         <code>AssetSchema</code> instance.
     */
    public ImmutableMap<AssetType, ImmutableSet<TracedEntry<Identifier>>> getAssetDependencies() {

        return new ImmutableMap<>(assetDependencies.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> new ImmutableSet<>(entry.getValue()), (a, b) -> a, HashMap::new)));
    }

    /**
     * Creates a new instance of the <code>AssetSchema</code> class.
     * 
     * @param path <code>TracedPath</code>: The path to this
     *             <code>AssetSchema</code> instance.
     * @param type <code>AssetType</code>: The type of this code>AssetSchema</code>
     *             instance, represented by a <code>AssetType</code> enum.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>AssetSchema</code> instance.
     */
    public AssetSchema(TracedPath path, AssetType type) throws LoggedException {

        this.type = type;

        TracedDictionary json = JSONOperator.retrieveJSON(path);

        identifierEntry = json.getAsMetadata("metadata", false, false);
        identifier = identifierEntry.getValue();

        TracedEntry<TracedDictionary> attributesEntry = json.getAsDict("attributes", false);
        attributes = createAttributes(attributesEntry.getValue(), true);

        HashMap<String, AssetAttributes> permutationsMap = new HashMap<>();

        TracedEntry<TracedDictionary> permutationsEntry = json.getAsDict("permutations", true);
        if (permutationsEntry.containsValue()) {

            TracedDictionary permutationsJson = permutationsEntry.getValue();
            for (String permutationKey : permutationsJson) {

                TracedEntry<TracedDictionary> permutationEntry = permutationsJson.getAsDict(permutationKey, false);
                TracedDictionary permutationJson = permutationEntry.getValue();
                permutationsMap.put(permutationKey, createAttributes(permutationJson, false));
            }
        }
        permutations = new ImmutableMap<>(permutationsMap);

        HashMap<String, ImmutableList<AssetEvent>> eventsMap = new HashMap<>();

        TracedEntry<TracedDictionary> eventsEntry = json.getAsDict("events", true);
        if (eventsEntry.containsValue()) {

            TracedDictionary eventsJson = eventsEntry.getValue();

            ArrayList<TracedEntry<String>> eventEntries = new ArrayList<>();

            for (String eventKey : eventsJson) {

                ArrayList<AssetEvent> eventSet = new ArrayList<>();

                eventsJson.compute(eventKey, List.of(eventsJson.dictCase(entry -> {

                    TracedDictionary eventJson = entry.getValue();
                    eventSet.add(new AssetEvent(eventJson));
                    return null;
                }),

                        eventsJson.arrayCase(entry -> {
                            TracedArray eventJson = entry.getValue();

                            for (int i : eventJson) {

                                eventSet.add(new AssetEvent(eventJson.getAsDict(i, false).getValue()));
                            }

                            return null;
                        })));

                for (AssetEvent event : eventSet) {

                    // If a permutation is referenced but was not defined, an error should be
                    // raised.
                    for (TracedEntry<String> permutationEntry : event.getAdd()) {

                        if (!permutations.containsKey(permutationEntry.getValue())) {

                            throw new ReferenceWithoutDefinitionException(permutationEntry, "Permutation");
                        }
                    }

                    // If a permutation is referenced but was not defined, an error should be
                    // raised.
                    for (TracedEntry<String> permutationEntry : event.getRemove()) {

                        if (!permutations.containsKey(permutationEntry.getValue())) {

                            throw new ReferenceWithoutDefinitionException(permutationEntry, "Permutation");
                        }
                    }

                    // A event which calls itself will recurse infinitely.
                    // 2 events calling each other could still cause infinite recursion, but that's
                    // more unlikely and will be dealt with if necessary.
                    for (TracedEntry<String> runEvent : event.getRun()) {

                        if (eventKey.equals(runEvent.getValue())) {

                            throw new UnexpectedValueException(runEvent);
                        }

                        eventEntries.add(runEvent);
                    }
                }

                eventsMap.put(eventKey, new ImmutableList<>(eventSet));
            }

            for (TracedEntry<String> eventEntry : eventEntries) {

                // If an event is referenced but was not defined, an error should be raised.
                if (!eventsMap.containsKey(eventEntry.getValue())) {

                    throw new ReferenceWithoutDefinitionException(eventEntry, "Event");
                }
            }
        }
        events = new ImmutableMap<>(eventsMap);

        HashMap<String, Object> propertiesMap = new HashMap<>();

        TracedEntry<TracedDictionary> propertiesEntry = json.getAsDict("properties", true);
        if (propertiesEntry.containsValue()) {

            TracedDictionary propertiesJson = propertiesEntry.getValue();

            for (String property : propertiesJson) {

                TracedEntry<Object> propertyEntry = propertiesJson.getAsScalar(property, true, null);
                propertiesMap.put(property, propertyEntry.getValue());
            }
        }

        properties = new ImmutableMap<>(propertiesMap);
    }

    /**
     * Creates an attribute set of this <code>AssetSchema</code> instance.
     * 
     * @param jsonSchema <code>TracedDictionary</code>: The dictionary used to
     *                   create the attribute set.
     * @param isBase     <code>boolean</code>: Whether or not the attributes being
     *                   built are
     * @return <code>AssetAttributes</code>: The generated attribute set.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         attribute set.
     */
    private AssetAttributes createAttributes(TracedDictionary jsonSchema, boolean isBase) throws LoggedException {

        return type.createAttributes(this, jsonSchema, isBase);
    }

    /**
     * Returns the string representation of this <code>AssetSchema</code> instance.
     * 
     * @return <code>String</code>: This <code>AssetSchema</code> instance in the
     *         following string representation: <br>
     *         "<code>namespace:identifier</code>"
     */
    @Override
    public String toString() {

        return identifier.toString();
    }
}
