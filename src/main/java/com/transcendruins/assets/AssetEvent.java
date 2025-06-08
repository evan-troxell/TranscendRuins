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

package com.transcendruins.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.scripts.TRScriptValue;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>AssetEvent</code>: A class representing an event which can be triggered
 * by an asset instance.
 */
public final class AssetEvent {

    public static final String ON_INITIALIZATION = "TranscendRuins:onInitialized";

    public static final String ON_TICK = "TranscendRuins:onTick";

    public static final String ON_DESTRUCTION = "TranscendRuins:onDestroyed";

    /**
     * <code>ImmutableList&lt;TRScript&gt;</code>: The conditions required to be
     * passed to execute this <code>AssetEvent</code> instance.
     */
    private final ImmutableList<TRScriptValue> conditions;

    /**
     * <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The permutations
     * to add when this <code>AssetEvent</code> instance is triggered.
     */
    private final ImmutableList<TracedEntry<String>> add;

    /**
     * Retrieves the permutations to add when this <code>AssetEvent</code> instance
     * is triggered.
     * 
     * @return <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The
     *         <code>add</code> field of this code>AssetEvent</code> instance.
     */
    public ImmutableList<TracedEntry<String>> getAdd() {

        return add;
    }

    /**
     * <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The permutations
     * to remove when this code>AssetEvent</code> instance is triggered.
     */
    private final ImmutableList<TracedEntry<String>> remove;

    /**
     * Retrieves the permutations to remove when this <code>AssetEvent</code>
     * instance is triggered.
     * 
     * @return <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The
     *         <code>remove</code> field of this code>AssetEvent</code> instance.
     */
    public ImmutableList<TracedEntry<String>> getRemove() {

        return remove;
    }

    /**
     * <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The events to
     * run when this code>AssetEvent</code> instance is triggered.
     */
    private final ImmutableList<TracedEntry<String>> run;

    /**
     * Retrieves the events to run when this <code>AssetEvent</code> instance is
     * triggered.
     * 
     * @return <code>ImmutableList&lt;TracedEntry&lt;String&gt;&gt;</code>: The
     *         <code>run</code> field of this code>AssetEvent</code> instance.
     */
    public ImmutableList<TracedEntry<String>> getRun() {

        return run;
    }

    private final ImmutableMap<String, Object> set;

    public ImmutableMap<String, Object> getSet() {

        return set;
    }

    public AssetEvent(TracedDictionary json) throws LoggedException {

        ArrayList<TRScriptValue> conditionsList = new ArrayList<>();

        if (json.containsKey("condition")) {

            conditionsList.add(new TRScriptValue(json, "condition"));
        }

        TracedEntry<TracedArray> conditionsEntry = json.getAsArray("conditions", true);
        if (conditionsEntry.containsValue()) {

            TracedArray conditionsJson = conditionsEntry.getValue();

            for (int i : conditionsJson) {

                conditionsList.add(new TRScriptValue(conditionsJson, i));
            }
        }

        conditions = new ImmutableList<>(conditionsList);

        ArrayList<TracedEntry<String>> addList = new ArrayList<>();
        ArrayList<TracedEntry<String>> removeList = new ArrayList<>();

        TracedEntry<TracedDictionary> permutationsEntry = json.getAsDict("permutations", true);
        if (permutationsEntry.containsValue()) {

            TracedDictionary permutationsJson = permutationsEntry.getValue();

            TracedEntry<TracedArray> addEntry = permutationsJson.getAsArray("add", true);
            if (addEntry.containsValue()) {

                TracedArray addJson = addEntry.getValue();
                for (int i : addJson) {

                    TracedEntry<String> permutationEntry = addJson.getAsString(i, false, null);
                    addList.add(permutationEntry);
                }
            }

            TracedEntry<TracedArray> removeEntry = permutationsJson.getAsArray("remove", true);
            if (removeEntry.containsValue()) {

                TracedArray removeJson = removeEntry.getValue();
                for (int i : removeJson) {

                    TracedEntry<String> permutationEntry = removeJson.getAsString(i, false, null);
                    removeList.add(permutationEntry);
                }
            }
        }

        add = new ImmutableList<>(addList);
        remove = new ImmutableList<>(removeList);

        ArrayList<TracedEntry<String>> runList = new ArrayList<>();

        TracedEntry<TracedArray> runEntry = json.getAsArray("run", true);
        if (runEntry.containsValue()) {

            TracedArray runJson = runEntry.getValue();

            for (int i : runJson) {

                runList.add(runJson.getAsString(i, false, null));
            }
        }

        run = new ImmutableList<>(runList);

        HashMap<String, Object> setMap = new HashMap<>();

        TracedEntry<TracedDictionary> setEntry = json.getAsDict("set", true);
        if (setEntry.containsValue()) {

            TracedDictionary setJson = setEntry.getValue();
            for (String key : setJson) {

                setMap.put(key, setJson.getAsScalar(key, true, null).getValue());
            }
        }

        set = new ImmutableMap<>(setMap);
    }

    public boolean execute(AssetInstance asset) {

        for (TRScriptValue condition : conditions) {

            if (!condition.evaluateBoolean(asset)) {

                return false;
            }
        }

        for (Map.Entry<String, Object> propertyEntry : set.entrySet()) {

            asset.setPublicProperty(propertyEntry.getKey(), propertyEntry.getValue());
        }

        asset.removePermutations(TracedEntry.unboxValues(remove));
        asset.addPermutations(TracedEntry.unboxValues(add));

        asset.updateAttributes();

        for (String event : TracedEntry.unboxValues(run)) {

            asset.executeEvent(event);
        }

        return true;
    }
}
