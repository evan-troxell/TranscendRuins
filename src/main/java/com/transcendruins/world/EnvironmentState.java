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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.contentmodules.packs.Pack;
import com.transcendruins.contentmodules.resources.Resource;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.resources.sounds.Sound;
import com.transcendruins.resources.sounds.SoundSet;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.resources.textures.TextureSet;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.sound.StoredSound;

/**
 * <code>EnvironmentState</code>: A class representing a the environment of a
 * world.
 */
public final class EnvironmentState {

    private final ImmutableList<Pack> packs;

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * The merged asset schemas of this <code>EnvironmentState</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

    /**
     * <code>ImmutableMap&lt;String, ImmutableMap&lt;String, String&gt;&gt;</code>:
     * The merged languages of this <code>EnvironmentState</code> instance.
     */
    private ImmutableMap<String, ImmutableMap<String, String>> languages;

    /**
     * Checks whether or not this <code>EnvironmentState</code> instance contains a
     * specific text.
     * 
     * @param language <code>String</code>: The language to check for.
     * @param text     <code>String</code>: The text to check for.
     * @return <code>boolean</code>: Whether or not the text was found.
     */
    public boolean containsText(String language, String text) {

        return languages.getOrDefault(language, new ImmutableMap<>()).containsKey(text);
    }

    /**
     * Retreives a specific text from this <code>EnvironmentState</code>
     * instance.
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
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Sound&gt;&gt;</code>:
     * The merged sounds of this <code>EnvironmentState</code> instance.
     */
    private ImmutableMap<AssetType, ImmutableMap<String, Sound>> sounds;

    /**
     * Checks whether or not this <code>EnvironmentState</code> instance contains a
     * specific sound.
     * 
     * @param type  <code>AssetType</code>: The asset type to check for.
     * @param sound <code>String</code>: The sound to check for.
     * @return <code>boolean</code>: Whether or not the sound was found.
     */
    public boolean containsSound(AssetType type, String sound) {

        return sounds.get(type).containsKey(sound);
    }

    /**
     * Retreives a specific sound from this <code>EnvironmentState</code>
     * instance.
     * 
     * @param type   <code>AssetType</code>: The asset type to check for.
     * @param sound  <code>String</code>: The sound to check for.
     * @param random <code>double</code>: The random ID key to use, in the range
     *               of <code>[0.0, 1.0]</code>.
     * @return <code>StoredSound</code>: The resulting sound.
     */
    public StoredSound getSound(AssetType type, String sound, double random) {

        if (!containsSound(type, sound)) {

            return SoundSet.MISSING_SOUND.retrieveSound();
        }

        return sounds.get(type).get(sound).getSound(random);
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;String, Texture&gt;&gt;</code>:
     * The merged textures of this <code>EnvironmentState</code> instance.
     */
    private ImmutableMap<AssetType, ImmutableMap<String, Texture>> textures;

    /**
     * Checks whether or not this <code>EnvironmentState</code> instance contains a
     * specific texture.
     * 
     * @param type    <code>AssetType</code>: The asset type to check for.
     * @param texture <code>String</code>: The texture to check for.
     * @return <code>boolean</code>: Whether or not the texture was found.
     */
    public boolean containsTexture(AssetType type, String texture) {

        return textures.get(type).containsKey(texture);
    }

    /**
     * Retreives a specific texture from this <code>EnvironmentState</code>
     * instance.
     * 
     * @param type    <code>AssetType</code>: The asset type to check for.
     * @param texture <code>String</code>: The texture to check for.
     * @param random  <code>double</code>: The random key to use, in the range
     *                of <code>[0.0, 1.0]</code>.
     * @return <code>Image</code>: The resulting texture.
     */
    public ImageIcon getTexture(AssetType type, String texture, double random) {

        if (!containsTexture(type, texture)) {

            return TextureSet.MISSING_TEXTURE.retrieveImage();
        }

        return textures.get(type).get(texture).getTexture(random);
    }

    /**
     * Retrieves an asset schema from this <code>EnvironmentState</code> instance.
     * 
     * @param type       <code>AssetType</code>: The type of asset schema to
     *                   retrieve.
     * @param identifier <code>Identifier</code>: The identifier of the asset schema
     *                   to retrieve.
     * @return <code>AssetSchema</code>: The asset schema retrieved from the
     *         <code>mergedAssets</code> field of this <code>EnvironmentState</code>
     *         instance.
     */
    public AssetSchema getSchema(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    /**
     * Creates a new instance of the <code>EnvironmentState</code> class.
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to
     *                  create this <code>EnvironmentState</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The texture set
     *                  used to create this <code>EnvironmentState</code>
     *                  instance.
     */
    EnvironmentState(List<Pack> packs, List<Resource> resources) {

        this.packs = new ImmutableList<>(packs);

        HashMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assetsMap = AssetType.createAssetMap(type -> {

            return new ImmutableMap<>(packs.stream()
                    .map(pack -> pack.getAssets().get(type)) // Retrieve the map of assets.
                    .flatMap(map -> map.entrySet().stream()) // Flatten the asset maps.
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (_, replacement) -> replacement // If the same asset is contained in multiple packs, use the
                                                            // highest one on the stack.
            )));
        });

        assets = new ImmutableMap<>(assetsMap);

        applyResources(resources);
    }

    public void applyResources(List<Resource> resources) {

        List<ResourceSet> stack = packs.stream().map(pack -> pack.getResources()).collect(Collectors.toList());
        stack.addAll(packs.stream().map(pack -> pack.getResources()).collect(Collectors.toList()));

        languages = ResourceSet.compileLanguages(stack);
        sounds = ResourceSet.compileSounds(stack);
        textures = ResourceSet.compileTextures(stack);
    }

    /**
     * Retrieves the environment state of the current world.
     * 
     * @return <code>EnvironmentState</code>: The <code>environment</code> field of
     *         the <code>world</code> field of the <code>World</code> class.
     */
    public static EnvironmentState getEnvironment() {

        return World.getWorld().getEnvironment();
    }
}
