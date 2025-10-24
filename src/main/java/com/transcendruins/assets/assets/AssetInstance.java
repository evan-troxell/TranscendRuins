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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.transcendruins.assets.AssetEvent;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.World;

/**
 * <code>AssetInstance</code>: A class representing a generated instance of any
 * asset type, including but not limited to: layouts, elements, entities, items,
 * and more.
 */
public abstract class AssetInstance extends Instance {

    /**
     * <code>AssetSchema</code>: The schema used to generate this
     * <code>AssetInstance</code> instance.
     */
    private final AssetSchema assetSchema;

    /**
     * <code>AssetPresets</code>: The presets used to generate this
     * <code>AssetInstance</code> instance.
     */
    private final AssetPresets assetPresets;

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
     * <code>long</code>: The randomized id of this <code>AssetInstance</code>
     * instance.
     */
    private final long randomId;

    /**
     * Retreives the randomized id of this <code>AssetInstance</code> instance.
     * 
     * @return <code>long</code>: The <code>randomId</code> field of this
     *         <code>AssetInstance</code> instance.
     */
    public final long getRandomId() {

        return randomId;
    }

    /**
     * <code>DeterministicRandom</code>: The random number generator (RNG) of this
     * <code>AssetInstance</code> instance.
     */
    private final DeterministicRandom random;

    /**
     * Retreives the next random value from the RNG of this
     * <code>AssetInstance</code> instance.
     * 
     * @return <code>long</code>: The next value of the <code>random</code> field of
     *         this <code>AssetInstance</code> instance.
     */
    public final long nextRandom() {

        return random.next();
    }

    /**
     * Shuffles a list using the RNG of this <code>AssetInstance</code> instance.
     * 
     * @param list <code>List&lt;?&gt;</code>: The list to shuffle.
     */
    public final void shuffle(List<?> list) {

        for (int i = list.size() - 1; i > 0; i--) {

            int j = (int) (DeterministicRandom.toDouble(nextRandom()) * (i + 1));
            Collections.swap(list, i, j);
        }
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
     * Sets the parent of this <code>PropertyHolder</code> instance to another
     * asset, or the <code>World</code> instance if another parent asset is not
     * provided.
     * 
     * @param parent <code>AssetInstance</code>: The parent of this
     *               <code>AssetInstance</code> instance.
     */
    protected final void setParent(AssetInstance parent) {

        super.setParent(parent == null ? parent : world);
    }

    private final ArrayList<String> addPermutations = new ArrayList<>();

    private final HashSet<String> removePermutations = new HashSet<>();

    /**
     * <code>ArrayList&lt;String&gt;</code>: The list of currently applied attribute
     * sets.
     */
    private final ArrayList<String> appliedPermutations = new ArrayList<>();

    /**
     * <code>boolean</code>: Whether or not this <code>AssetInstance</code> has been
     * initialized yet.
     */
    private boolean isInitialized = false;

    /**
     * Creates a new instance of the <code>AssetInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>AssetInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public AssetInstance(AssetContext assetContext, Object key) {

        if (key != AssetType.KEY) {

            throw new IllegalArgumentException("Invalid instantiation key passed to asset instance.");
        }

        assetPresets = assetContext.getPresets();

        world = assetContext.getWorld();
        setProperty("world", world);

        setParent(assetContext.getParent());

        randomId = assetContext.getRandomId();
        random = new DeterministicRandom(randomId);

        type = assetPresets.getType();
        setProperty("type", type.toString());

        identifier = assetPresets.getIdentifier();
        setProperty("identifier", identifier.toString());

        assetSchema = world.getSchema(type, identifier);

        if (assetSchema == null) {

            throw new IllegalArgumentException("Missing schema passed to asset instance.");
        }

        for (Map.Entry<String, Object> entry : assetSchema.getProperties().entrySet()) {

            String property = entry.getKey();
            Object value = entry.getValue();
            setPublicProperty(property, value);
        }

        for (Map.Entry<String, Object> entry : assetPresets.getProperties().entrySet()) {

            String property = entry.getKey();
            Object value = entry.getValue();
            setPublicProperty(property, value);
        }
    }

    public final void initialize() {

        if (isInitialized) {

            return;
        }

        updateAttributes();
        isInitialized = true;

        executeEvent(AssetEvent.ON_INITIALIZATION);

        for (String event : assetPresets.getEvents()) {

            executeEvent(event);
        }
    }

    public final void reload() {

        isInitialized = false;
        initialize();
    }

    /**
     * Adds a list of permutations to this <code>AssetInstance</code> instance.
     * 
     * @param permutations <code>List&lt;String&gt;</code>: The permutations to add.
     */
    public final void addPermutations(List<String> permutations) {

        removePermutations.removeAll(permutations);
        addPermutations.removeAll(permutations);
        addPermutations.addAll(permutations);
    }

    /**
     * Removes a list of permutations from this <code>AssetInstance</code> instance.
     * 
     * @param permutations <code>List&lt;String&gt;</code>: The permutations to
     *                     remove.
     */
    public final void removePermutations(List<String> permutations) {

        addPermutations.removeAll(permutations);
        removePermutations.addAll(permutations);
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

        if (!assetSchema.containsEvent(eventName)) {

            return executed;
        }

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

        // If there are no existing permutations to remove, only apply the added
        // permutations
        if (isInitialized
                && (removePermutations.isEmpty() || Collections.disjoint(appliedPermutations, removePermutations))) {

            for (String permutationKey : addPermutations) {

                AssetAttributes permutation = assetSchema.getPermutation(permutationKey);
                applyAttributes(permutation);
            }

            // Update the applied permutations.
            appliedPermutations.removeAll(addPermutations);
            appliedPermutations.addAll(addPermutations);

            // Clear the permutation updates.
            addPermutations.clear();
            removePermutations.clear();

            return;
        }

        // Update the applied permutations.
        appliedPermutations.removeAll(removePermutations);
        appliedPermutations.removeAll(addPermutations);
        appliedPermutations.addAll(addPermutations);

        // Clear the permutation updates.
        removePermutations.clear();
        addPermutations.clear();

        // Apply the base attribute layer.
        applyAttributes(assetSchema.calculateAttributes());

        // Update permutations
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

        executeEvent(AssetEvent.ON_TICK);
        onUpdate(time);
    }

    /**
     * Performs all instance updates of this <code>PrimaryAssetInstance</code>
     * instance.
     * 
     * @param time <code>double</code> The time since the world was created.
     */
    protected abstract void onUpdate(double time);

    /**
     * Retrieves a texture based on the random id and type of this
     * <code>AssetInstance</code> instance.
     * 
     * @param texture <code>String</code>: The texture to retrieve.
     * @return <code>ImageIcon</code>: The retrieved texture icon. Note that this
     *         value is NOT shared between any other asset instances.
     */
    public final ImageIcon getInstanceTexture(String texture) {

        return getWorld().getTexture(texture, randomId);
    }

    /**
     * Retrieves a texture based on the random id and type of this
     * <code>AssetInstance</code> instance and maps it into a new
     * <code>BufferedImage</code> instance.
     * 
     * @param texture   <code>String</code>: The texture to retrieve.
     * @param imageType <code>int</code>: The image type to parse the image into.
     *                  This should be a constant from the
     *                  <code>BufferedImage</code> class, such as
     *                  <code>BufferedImage.TYPE_INT_ARGB</code>.
     * @return <code>BufferedImage</code>: The retrieved texture icon. Note that
     *         this value is NOT shared between any other asset instances.
     */
    public final BufferedImage getInstanceTextureAsBufferedImage(String texture, int imageType) {

        ImageIcon icon = getInstanceTexture(texture);

        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), imageType);

        Graphics g = bufferedImage.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);
        g.dispose();

        return bufferedImage;
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
    public final boolean isLikeAsset(AssetInstance asset) {

        return type == asset.getType() && identifier == asset.getIdentifier();
    }

    @Override
    public final int hashCode() {

        return identifier.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {

        // If the object does not exist or is not an asset.
        if (obj == null || !(obj instanceof AssetInstance)) {

            return false;
        }

        return this == obj;
    }
}
