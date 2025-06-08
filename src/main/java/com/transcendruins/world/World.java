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

package com.transcendruins.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import com.transcendruins.PropertyHolder;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.resources.sounds.Sound;
import com.transcendruins.resources.sounds.SoundSet;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.resources.textures.TextureSet;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.sound.StoredSound;

/**
 * <code>World</code>: A class representing a loaded world environment.
 */
public final class World extends PropertyHolder {

    /**
     * <code>int</code>: The length and width of a unit tile.
     */
    public static final int UNIT_TILE = 20;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>East</code>.
     */
    public static final int NORTH = 0;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>North</code>.
     */
    public static final int EAST = 90;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>West</code>.
     */
    public static final int SOUTH = 180;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>South</code>.
     */
    public static final int WEST = 270;

    /**
     * <code>World</code>: The current world of the program.
     */
    private static World world;

    /**
     * Retrieves the current world of the program.
     * 
     * @return <code>World</code>: The <code>world</code> field
     */
    public static World getWorld() {

        return world;
    }

    /**
     * <code>UUID</code>: The unique identifier of this <code>World</code> instance.
     */
    private final UUID uuid;

    /**
     * Retrieves the UUID of this <code>World</code> instance.
     * 
     * @return <code>UUID</code>: The <code>uuid</code> field of this
     *         <code>World</code> instance.
     */
    public UUID getUuid() {

        return uuid;
    }

    /**
     * <code>Random</code>: The random number generator (RNG) of this
     * <code>World</code> instance.
     */
    private final Random random = new Random();

    /**
     * Retreives the next random double from the RNG of this <code>World</code>
     * instance.
     * 
     * @return <code>double</code>: The next double value of the <code>random</code>
     *         field of this <code>World</code> instance.
     */
    public double nextRandom() {

        return random.nextDouble();
    }

    /**
     * Shuffles the contents of a collection.
     * 
     * @param <K>    The collection type to shuffle.
     * @param values <code>List&lt;K&gt;</code>: The values to shuffle.
     * @return <code>List&lt;K&gt;</code>: The shuffled list.
     */
    public <K> List<K> shuffle(List<K> values) {

        Collections.shuffle(values, random);

        return values;
    }

    /**
     * <code>long</code>: The time of creation of this <code>World</code> instance.
     */
    private long timeOfCreation;

    /**
     * Retrieve the current time in millis since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The <code>timeOfCreation</code> field subtracted
     *         from the current time in milliseconds.
     */
    public long getRuntimeMillis() {

        return initialized ? System.currentTimeMillis() - timeOfCreation : 0;
    }

    /**
     * Retrieve the current time in seconds since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The current runtime milliseconds divided by
     *         1000.0.
     */
    public double getRuntimeSeconds() {

        return getRuntimeMillis() / 1000.0;
    }

    private final ImmutableList<ContentPack> packs;

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * The merged asset schemas of this <code>World</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

    /**
     * <code>ImmutableMap&lt;String, ImmutableMap&lt;String, String&gt;&gt;</code>:
     * The merged languages of this <code>World</code> instance.
     */
    private ImmutableMap<String, ImmutableMap<String, String>> languages;

    /**
     * Checks whether or not this <code>World</code> instance contains a specific
     * text.
     * 
     * @param language <code>String</code>: The language to check for.
     * @param text     <code>String</code>: The text to check for.
     * @return <code>boolean</code>: Whether or not the text was found.
     */
    public boolean containsText(String language, String text) {

        return languages.getOrDefault(language, new ImmutableMap<>()).containsKey(text);
    }

    /**
     * Retreives a specific text from this <code>World</code> instance.
     * 
     * @param language <code>String</code>: The language to check for.
     * @param text     <code>String</code>: The text to check for.
     * @return <code>String</code>: The resulting text.
     */
    public String getText(String language, String text) {

        if (!containsText(language, text)) {

            return text;
        }

        return languages.get(language).get(text);
    }

    /**
     * <code>ImmutableMap&lt;String, Sound&gt;</code>: The merged sounds of this
     * <code>World</code> instance.
     */
    private ImmutableMap<String, Sound> sounds;

    /**
     * <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The paths of the sounds
     * of this <code>World</code> instance.
     */
    private ImmutableMap<String, TracedPath> soundPaths;

    /**
     * Checks whether or not this <code>World</code> instance contains a specific
     * sound.
     * 
     * @param sound <code>String</code>: The sound to check for.
     * @return <code>boolean</code>: Whether or not the sound was found.
     */
    public boolean containsSound(String sound) {

        return sounds.containsKey(sound);
    }

    /**
     * Retreives a specific sound from this <code>World</code> instance.
     * 
     * @param sound  <code>String</code>: The sound to check for.
     * @param random <code>double</code>: The random ID key to use, in the range of
     *               <code>[0.0, 1.0]</code>.
     * @return <code>StoredSound</code>: The resulting sound.
     */
    public StoredSound getSound(String sound, double random) {

        if (!containsSound(sound)) {

            return SoundSet.MISSING_SOUND.retrieveAudio();
        }

        StoredSound audio = sounds.get(sound).getSound(random, soundPaths);
        return audio != null ? audio : SoundSet.MISSING_SOUND.retrieveAudio();
    }

    /**
     * <code>ImmutableMap&lt;String, Texture&gt;</code>: The merged textures of this
     * <code>World</code> instance.
     */
    private ImmutableMap<String, Texture> textures;

    /**
     * <code>ImmutableMap&lt;String, TracedPath&gt;</code>: The paths of the
     * textures of this <code>World</code> instance.
     */
    private ImmutableMap<String, TracedPath> texturePaths;

    /**
     * Checks whether or not this <code>World</code> instance contains a specific
     * texture.
     * 
     * @param texture <code>String</code>: The texture to check for.
     * @return <code>boolean</code>: Whether or not the texture was found.
     */
    public boolean containsTexture(String texture) {

        return textures.containsKey(texture);
    }

    /**
     * Retreives a specific texture from this <code>World</code> instance.
     * 
     * @param texture <code>String</code>: The texture to check for.
     * @param random  <code>double</code>: The random key to use, in the range of
     *                <code>[0.0, 1.0]</code>.
     * @return <code>Image</code>: The resulting texture.
     */
    public ImageIcon getTexture(String texture, double random) {

        if (!containsTexture(texture)) {

            return TextureSet.MISSING_TEXTURE.retrieveImage();
        }

        ImageIcon icon = textures.get(texture).getTexture(random, texturePaths);
        return icon != null ? icon : TextureSet.MISSING_TEXTURE.retrieveImage();
    }

    /**
     * Retrieves an asset schema from this <code>World</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset schema to
     *                   retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset schema
     *                   to retrieve.
     * @return <code>AssetSchema</code>: The asset schema retrieved from the
     *         <code>mergedAssets</code> field of this <code>World</code> instance.
     */
    public AssetSchema getSchema(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    private boolean initialized = false;

    public void start() {

        timeOfCreation = System.currentTimeMillis();
        initialized = true;
    }

    public void end() {

        timeOfCreation = -1;
        initialized = false;
    }

    /**
     * Creates a new instance of the <code>World</code> class.
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create this
     *                  <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources used to
     *                  create this <code>World</code> instance.
     */
    private World(List<ContentPack> packs, List<ResourcePack> resources) {

        uuid = UUID.randomUUID();

        this.packs = new ImmutableList<>(packs);

        HashMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assetsMap = AssetType.createAssetMap(type -> {

            return new ImmutableMap<>(packs.stream().map(pack -> pack.getAssets().get(type)) // Retrieve the map of
                                                                                             // assets.
                    .flatMap(map -> map.entrySet().stream()) // Flatten the asset maps.
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement
            // If the same asset is contained in multiple packs, use the highest one on the
            // stack.
            )));
        });

        assets = new ImmutableMap<>(assetsMap);

        applyResources(resources);

        setProperty("test", "test");
        setProperty("test2", new ImmutableMap<>(Map.of("test", "test2")));
    }

    public void applyResources(List<ResourcePack> resources) {

        List<ResourceSet> stack = packs.stream().map(pack -> pack.getResources()).collect(Collectors.toList());
        stack.addAll(packs.stream().map(pack -> pack.getResources()).collect(Collectors.toList()));

        languages = ResourceSet.compileLanguages(stack);

        sounds = ResourceSet.compileSounds(stack);
        soundPaths = ResourceSet.compilePaths(stack, pack -> pack.getSounds().getPaths());

        textures = ResourceSet.compileTextures(stack);
        texturePaths = ResourceSet.compilePaths(stack, pack -> pack.getTextures().getPaths());
    }

    /**
     * Creates a new instance of the <code>World</code> class and assigns it to the
     * <code>world</code> field
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create the
     *                  new <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources used to
     *                  create the new <code>World</code> instance.
     * @return <code>World</code>: The generated world.
     */
    public static World createWorld(List<ContentPack> packs, List<ResourcePack> resources) {

        world = new World(packs, resources);
        return world;
    }
}
