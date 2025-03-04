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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.transcendruins.assets.AssetEvent;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;

/**
 * <code>AssetInstance</code>: A class representing a generated instance of any
 * asset type, including but not limited to: layouts, elements, entities,
 * items, and more.
 */
public abstract class AssetInstance extends Instance {

    /**
     * <code>AssetSchema</code>: The schema used to generate this
     * <code>AssetInstance</code> instance.
     */
    private final AssetSchema assetSchema;

    /**
     * <code>World</code>: The world copy of this <code>AssetInstance</code>
     * instance.
     */
    private final World world;

    /**
     * Retrieves the world copy of this <code>AssetInstance</code> instance.
     * 
     * @return <code>World</code>: The <code>world</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final World getWorld() {

        return world;
    }

    /**
     * <code>double</code>: The randomized ID of this <code>AssetInstance</code>
     * instance, in the range of <code>[0.0, 1.0]</code>.
     */
    private final double randomId;

    /**
     * Retreives the randomized ID of this <code>AssetInstance</code> instance.
     * 
     * @return <code>double</code>: The <code>randomId</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final double getRandomId() {

        return randomId;
    }

    /**
     * <code>AssetType</code>: The asset type of this <code>AssetInstance</code>
     * instance.
     */
    private final AssetType type;

    /**
     * Retrieves the asset type of this <code>AssetInstance</code> instance.
     * 
     * @return <code>AssetType</code>: The <code>type</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final AssetType getType() {

        return type;
    }

    /**
     * <code>Identifier</code>: The identifier of this <code>AssetInstance</code>
     * instance.
     */
    private final Identifier identifier;

    /**
     * Retrieves the identifier of this <code>AssetInstance</code> instance.
     * 
     * @return <code>Identifier</code>: The <code>identifier</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final Identifier getIdentifier() {

        return identifier;
    }

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of currently applied attribute
     * sets.
     */
    private final ArrayList<String> appliedPermutations = new ArrayList<>();

    /**
     * <code>HashMap&lt;String, Object&gt;</code>: The set of private properties of
     * this
     * <code>AssetInstance</code> instance.
     */
    private final HashMap<String, Object> privateProperties = new HashMap<>();

    protected final void setProperty(String property, boolean value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, int value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, long value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, float value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, double value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, String value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, ImmutableList<?> value) {

        privateProperties.put(property, value);
    }

    protected final void setProperty(String property, ImmutableMap<String, ?> value) {

        privateProperties.put(property, value);
    }

    /**
     * <code>HashMap&lt;String, Object&gt;</code>: The set of public properties of
     * this
     * <code>AssetInstance</code> instance.
     */
    private final HashMap<String, Object> publicProperties = new HashMap<>();

    /**
     * Sets a public property of this <code>AssetInstance</code> instance.
     * 
     * @param property <code>String</code>: The property to set.
     * @param value    <code>Object</code>: The value to set.
     */
    public final void setPublicProperty(String property, Object value) {

        publicProperties.put(property, value);
    }

    /**
     * Determines whether or not this <code>AssetInstance</code> instance contains a
     * property.
     * 
     * @param property <code>String</code>: The property to check for.
     * @return <code>boolean</code>: Whether or not the property is contained within
     *         the <code>privateProperties</code> field of this
     *         <code>AssetInstance</code>
     *         instance.
     */
    public final boolean hasProperty(String property) {

        return privateProperties.containsKey(property) || publicProperties.containsKey(property);
    }

    /**
     * Retrieves a property from an asset, dictionary, or list using a pathway of
     * token values.
     * 
     * @param val    <code>Object</code>: The object whose value to retrieve. If the
     *               <code>tokens</code> parameter is empty, this is the value which
     *               will be returned; otherwise, if this is an asset, dictionary,
     *               or list, its property will be returned, and if it is NOT an
     *               indexable item, a <code>null</code> value will be returned.
     * @param tokens <code>String[]</code>: The token pathway to follow. If this has
     *               a length of <code>0</code>, the <code>val</code> parameter will
     *               be returned; otherwise, this pathway will be traced.
     * @return <code>Object</code>: The retrieved property.
     */
    private static Object getProperty(Object val, String[] tokens) {

        if (val == null || tokens.length == 0) {

            return val;
        }

        return switch (val) {

            case AssetInstance asset -> {

                yield asset.getProperty(tokens);
            }

            case Map<?, ?> mapVal -> {

                String next = tokens[0];

                yield getProperty(mapVal.get(next), Arrays.copyOfRange(tokens, 1, tokens.length));
            }

            case List<?> listVal -> {

                String next = tokens[0];
                int nextIndex;

                try {

                    nextIndex = Integer.parseInt(next);
                } catch (NumberFormatException e) {

                    yield null;
                }

                yield getProperty(listVal.get(nextIndex), Arrays.copyOfRange(tokens, 1, tokens.length));
            }

            default -> null;

        };
    }

    /**
     * Retrieves a property from this <code>AssetInstance</code> following a
     * property path.
     * 
     * @param tokens <code>String[]</code>: The property pathway to follow, split
     *               into tokens between the '<code>.</code>' character.
     * @return <code>Object</code>: The retrieved property.
     */
    private Object getProperty(String[] tokens) {

        if (tokens.length == 0) {

            return null;
        }

        String property = tokens[0];
        Object propertyVal = privateProperties.getOrDefault(property, publicProperties.get(property));

        // Check the parent value if the property could not be found.
        if (propertyVal == null && hasParent()) {

            return parent.getProperty(tokens);
        }

        String[] nextTokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        return getProperty(propertyVal, nextTokens);
    }

    /**
     * Retrieves a property from this <code>AssetInstance</code> instance.
     * 
     * @param property <code>String</code>: The property to retrieve.
     * @return <code>Object</code>: The retrieved property
     */
    public final Object getProperty(String property) {

        String[] tokens = property.split("\\.");

        if (tokens.length == 0) {

            return null;
        }

        return getProperty(tokens);
    }

    /**
     * <code>AssetInstance</code>: The parent asset to this
     * <code>AssetInstance</code> instance. This is not required to hold a value.
     */
    private AssetInstance parent;

    /**
     * Sets the parent of this <code>AssetInstance</code> instance.
     * 
     * @param parent <code>AssetInstance</code>: The parent of this
     *               <code>AssetInstance</code> instance.
     */
    protected final void setParent(AssetInstance parent) {

        this.parent = parent;
    }

    /**
     * Determines whether or not this <code>AssetInstance</code> instance has a
     * parent asset.
     * 
     * @return <code>boolean</code>: Whether or not the <code>parent</code> field of
     *         this <code>AssetInstance</code> instance is not null.
     */
    public final boolean hasParent() {

        return parent != null;
    }

    /**
     * Retrieves the parent asset to this <code>AssetInstance</code> instance.
     * 
     * @return <code>AssetInstance</code>: The <code>parent</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final AssetInstance getParent() {

        return parent;
    }

    /**
     * Creates a new instance of the <code>AssetInstance</code> class.
     * 
     * @param context <code>AssetContext</code>: The context used to generate this
     *                <code>AssetInstance</code> instance.
     */
    public AssetInstance(AssetContext context) {

        AssetPresets presets = context.getPresets();
        publicProperties.putAll(presets.getPublicProperties());
        privateProperties.putAll(presets.getPrivateProperties());

        world = context.getWorld();
        // setProperty("world", world);

        randomId = context.getRandomId();

        type = presets.getType();
        setProperty("type", type.toString());

        identifier = presets.getIdentifier();
        setProperty("identifier", identifier.toString());

        assetSchema = world.getEnvironment().getSchema(type, identifier);

        updateAttributes();
        executeEvent(AssetEvent.ON_INITIALIZATION);

        for (TracedEntry<String> eventEntry : presets.getEvents()) {

            String event = eventEntry.getValue();
            executeEvent(event);
        }
    }

    /**
     * Adds a list of permutations to this <code>AssetInstance</code> instance.
     * 
     * @param permutations <code>List&lt;String&gt;</code>: The permutations to
     *                     add.
     */
    public final void addPermutations(List<String> permutations) {

        appliedPermutations.addAll(permutations);
    }

    /**
     * Removes a list of permutations from this <code>AssetInstance</code>
     * instance.
     * 
     * @param permutations <code>List&lt;String&gt;</code>: The permutations to
     *                     remove.
     */
    public final void removePermutations(List<String> permutations) {

        appliedPermutations.removeAll(permutations);
    }

    /**
     * Executes an event in this <code>AssetInstance</code> instance.
     * 
     * @param eventName <code>String</code>: The name of the event to execute.
     * @return <code>boolean</code>: Whether or not the event was successfully
     *         executed.
     */
    public final boolean executeEvent(String eventName) {

        boolean executed = false;

        for (AssetEvent event : assetSchema.getEvent(eventName)) {

            if (event.execute(this)) {

                executed = true;
            }
        }

        if (executed) {

            updateAttributes();
        }

        return executed;
    }

    /**
     * Updates the attributes of this <code>AssetInstance</code> instance.
     */
    public final void updateAttributes() {

        applyAttributes(assetSchema.calculateAttributes());

        for (String permutationKey : appliedPermutations) {

            AssetAttributes permutation = assetSchema.getPermutation(permutationKey);
            applyAttributes(permutation);
        }
    }

    /**
     * Performs the update actions of this <code>AssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    public final void update(double time) {

        onUpdate(time);
        executeEvent(AssetEvent.ON_TICK);
    }

    /**
     * Performs all instance updates of this <code>ModelAssetInstance</code>
     * instance.
     * 
     * @param time <code>double</code> The time since the world was created.
     */
    protected abstract void onUpdate(double time);

    /**
     * Retrieves a texture based on the random ID and type of this
     * <code>AssetInstance</code> instance.
     * 
     * @param texture <code>String</code>: The texture to retrieve.
     * @return <code>ImageIcon</code>: The retrieved texture icon. Note that this
     *         value is NOT shared between any other asset instances.
     */
    public ImageIcon getTexture(String texture) {

        return getWorld().getEnvironment().getTexture(type, texture, randomId);
    }

    /**
     * Determines whether or not an asset is like this <code>AssetInstance</code>
     * instance.
     * 
     * @param asset <code>AssetInstance</code>: The asset to check.
     * @return <code>boolean</code>: Whether or not the <code>type</code> and
     *         <code>identifier</code> fields of this <code>AssetInstance</code>
     *         instance match those of the <code>asset</code> parameter.
     */
    public boolean isLikeAsset(AssetInstance asset) {

        return type == asset.getType() && identifier == asset.getIdentifier();
    }
}
