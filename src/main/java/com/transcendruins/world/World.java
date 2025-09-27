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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import com.transcendruins.PropertyHolder;
import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.catalogue.AssetCatalogue;
import com.transcendruins.assets.catalogue.events.GlobalEventSchema;
import com.transcendruins.assets.interfaces.InterfaceContext;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.assets.recipes.RecipeContext;
import com.transcendruins.assets.recipes.RecipeInstance;
import com.transcendruins.assets.recipes.RecipeSet;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.resources.languages.Language;
import com.transcendruins.resources.languages.LanguageSet;
import com.transcendruins.resources.sounds.Sound;
import com.transcendruins.resources.sounds.SoundSet;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.resources.textures.TextureSet;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.utilities.random.DeterministicRandom;
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
    public static final World getWorld() {

        return world;
    }

    /**
     * <code>long</code>: The seed of this <code>World</code> instance.
     */
    private final long seed;

    /**
     * Retrieves the seed of this <code>World</code> instance.
     * 
     * @return <code>long</code>: The <code>seed</code> field of this
     *         <code>World</code> instance.
     */
    public final long getSeed() {

        return seed;
    }

    /**
     * <code>DeterministicRandom</code>: The random number generator (RNG) of this
     * <code>World</code> instance.
     */
    private final DeterministicRandom random;

    /**
     * Retreives the next random value from the RNG of this <code>World</code>
     * instance.
     * 
     * @return <code>long</code>: The next value of the <code>random</code> field of
     *         this <code>World</code> instance.
     */
    public final long nextRandom() {

        return random.next();
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
    public final long getRuntimeMillis() {

        return initialized ? System.currentTimeMillis() - timeOfCreation : 0;
    }

    /**
     * Retrieve the current time in seconds since the time of creation of this
     * <code>World</code> instance.
     * 
     * @return <code>long</code>: The current runtime milliseconds divided by
     *         1000.0.
     */
    public final double getRuntimeSeconds() {

        return getRuntimeMillis() / 1000.0;
    }

    /**
     * <code>ImmutableMap&lt;AssetType, ImmutableMap&lt;Identifier, AssetSchema&gt;&gt;</code>:
     * The merged asset schemas of this <code>World</code> instance.
     */
    private final ImmutableMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assets;

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
    public final AssetSchema getSchema(AssetType type, Identifier identifier) {

        return assets.get(type).get(identifier);
    }

    private ImmutableMap<String, AssetPresets> locations;

    private ImmutableMap<String, ImmutableList<GlobalEventSchema>> events;

    private ImmutableMap<String, AssetPresets> overlays;

    public final HashMap<String, InterfaceInstance> getOverlays() {

        HashMap<String, InterfaceInstance> overlaysMap = new HashMap<>();
        for (String overlay : overlays.keySet()) {

            overlaysMap.put(overlay, createInterface(overlays.get(overlay)));
        }

        return overlaysMap;
    }

    private ImmutableMap<String, AssetPresets> menus;

    public final InterfaceInstance getMenu(String menu) {

        return createInterface(menus.get(menu));
    }

    private InterfaceInstance createInterface(AssetPresets presets) {

        InterfaceContext context = new InterfaceContext(presets, this, null, null);
        return (InterfaceInstance) AssetType.INTERFACE.createAsset(context);
    }

    private ImmutableMap<String, ImmutableMap<String, AssetPresets>> recipes;

    public final HashMap<String, RecipeInstance> getRecipes(String category) {

        if (!recipes.containsKey(category)) {

            return new HashMap<>();
        }

        HashMap<String, RecipeInstance> recipeMap = new HashMap<>();

        ImmutableMap<String, AssetPresets> categoryMap = recipes.get(category);
        for (String recipe : categoryMap.keySet()) {

            recipeMap.put(recipe, createRecipe(categoryMap.get(recipe)));
        }

        return recipeMap;
    }

    private RecipeInstance createRecipe(AssetPresets presets) {

        RecipeContext context = new RecipeContext(presets, world, null);
        return (RecipeInstance) AssetType.RECIPE.createAsset(context);
    }

    /**
     * <code>String</code>: The current language of this <code>World</code>
     * instance.
     */
    private String language;

    /**
     * Sets the current language of this <code>World</code> instance.
     * 
     * @param language <code>String</code>: The <code>language</code> field of this
     *                 <code>World</code> instance.
     */
    public final void setLanguage(String language) {

        this.language = language;
    }

    /**
     * Retrieves the current language of this <code>World</code> instance.
     * 
     * @return <code>String</code>: The <code>language</code> field of this
     *         <code>World</code> instance.
     */
    public final String getLanguage() {

        return language;
    }

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
    public final boolean containsText(String language, String text) {

        return languages.getOrDefault(language, new ImmutableMap<>()).containsKey(text);
    }

    /**
     * Retreives a specific text from this <code>World</code> instance in the
     * current language.
     * 
     * @param text <code>String</code>: The text to check for.
     * @return <code>String</code>: The resulting text.
     */
    public final String getText(String text) {

        if (!containsText(language, text)) {

            return text;
        }

        return languages.get(language).get(text);
    }

    /**
     * Retreives a specific text from this <code>World</code> instance.
     * 
     * @param language <code>String</code>: The language to check for.
     * @param text     <code>String</code>: The text to check for.
     * @return <code>String</code>: The resulting text.
     */
    public final String getText(String language, String text) {

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
    public final boolean containsSound(String sound) {

        return sounds.containsKey(sound);
    }

    /**
     * Retreives a specific sound from this <code>World</code> instance.
     * 
     * @param sound  <code>String</code>: The sound to check for.
     * @param random <code>long</code>: The random id key to use.
     * @return <code>StoredSound</code>: The resulting sound.
     */
    public final StoredSound getSound(String sound, long random) {

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
    public final boolean containsTexture(String texture) {

        return textures.containsKey(texture);
    }

    /**
     * Retreives a specific texture from this <code>World</code> instance.
     * 
     * @param texture <code>String</code>: The texture to check for.
     * @param random  <code>long</code>: The random id key to use.
     * @return <code>Image</code>: The resulting texture.
     */
    public final ImageIcon getTexture(String texture, long random) {

        if (!containsTexture(texture)) {

            return TextureSet.MISSING_TEXTURE.retrieveImage();
        }

        ImageIcon icon = textures.get(texture).getTexture(random, texturePaths);
        return icon != null ? icon : TextureSet.MISSING_TEXTURE.retrieveImage();
    }

    private StyleSet style;

    public final StyleSet getStyle() {

        return style;
    }

    private boolean initialized = false;

    public final void start() {

        timeOfCreation = System.currentTimeMillis();
        initialized = true;
    }

    public final void end() {

        timeOfCreation = -1;
        initialized = false;
    }

    /**
     * Creates a new instance of the <code>World</code> class and assigns it to the
     * <code>world</code> field.
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create the
     *                  new <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources used to
     *                  create the new <code>World</code> instance.
     * @param seed      <code>long</code>: The seed used to create the new
     *                  <code>World</code> instance.
     * @return <code>World</code>: The generated world.
     */
    public static final World createWorld(List<ContentPack> packs, List<ResourcePack> resources, long seed) {

        return world = new World(packs, resources, seed);
    }

    /**
     * Creates a new instance of the <code>World</code> class.
     * 
     * @param packs     <code>List&lt;Pack&gt;</code>: The packs used to create this
     *                  <code>World</code> instance.
     * @param resources <code>List&lt;Resource&gt;</code>: The resources used to
     *                  create this <code>World</code> instance.
     * @param seed      <code>long</code>: The seed to create the world using.
     */
    private World(List<ContentPack> packs, List<ResourcePack> resources, long seed) {

        this.seed = seed;
        random = new DeterministicRandom(seed);

        Stream<ContentPack> packStream = packs.stream();

        HashMap<AssetType, ImmutableMap<Identifier, AssetSchema>> assetsMap = AssetType.createAssetMap(type -> {

            return new ImmutableMap<>(packStream.map(pack -> pack.getAssets().get(type)) // Retrieve the map of
                                                                                         // assets.
                    .flatMap(map -> map.entrySet().stream()) // Flatten the asset maps.
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement
            // If the same asset is contained in multiple packs, use the highest one on the
            // stack.
            )));
        });

        assets = new ImmutableMap<>(assetsMap);

        // Apply the catalogues.
        Stream<AssetCatalogue> catalogueStream = packStream.map(ContentPack::getCatalogue);
        applyCatalogue(catalogueStream);

        // Join the content pack resources to the resource pack resources and apply.
        Stream<ResourceSet> resourceStream = Stream.concat(packStream.map(ContentPack::getResources),
                resources.stream().map(ResourcePack::getResources));
        applyResources(resourceStream);
    }

    public final void applyCatalogue(Stream<AssetCatalogue> catalogueStream) {

        locations = compile(catalogueStream, AssetCatalogue::getLocations);
        events = compile(catalogueStream, AssetCatalogue::getEvents);

        overlays = compile(catalogueStream, AssetCatalogue::getOverlays);
        menus = compile(catalogueStream, AssetCatalogue::getMenus);

        Stream<Map<String, RecipeSet>> recipeStream = catalogueStream.map(AssetCatalogue::getRecipes);
        recipes = compileGroup(recipeStream, RecipeSet::getRecipes);
    }

    public final void applyResources(Stream<ResourceSet> resourceStream) {

        Stream<Map<String, Language>> languageStream = resourceStream.map(ResourceSet::getLanguages)
                .map(LanguageSet::getLanguages);
        languages = new ImmutableMap<>(compileGroup(languageStream, Language::getMappings));

        Stream<SoundSet> soundStream = resourceStream.map(ResourceSet::getSounds);
        sounds = compile(soundStream, SoundSet::getSounds);
        soundPaths = compile(soundStream, SoundSet::getPaths);

        Stream<TextureSet> textureStream = resourceStream.map(ResourceSet::getTextures);
        textures = compile(textureStream, TextureSet::getTextures);
        texturePaths = compile(textureStream, TextureSet::getPaths);

        style = createStyle(resourceStream);
    }

    public static final <V, K> ImmutableMap<String, ImmutableMap<String, K>> compileGroup(
            Stream<Map<String, V>> resources, Function<V, Map<String, K>> mapper) {

        HashMap<String, ArrayList<V>> stack = new HashMap<>();
        resources.forEach(set -> {

            for (Map.Entry<String, V> mapEntry : set.entrySet()) {

                ArrayList<V> group = stack.computeIfAbsent(mapEntry.getKey(), _ -> new ArrayList<>());
                group.add(mapEntry.getValue());
            }
        });

        HashMap<String, ImmutableMap<String, K>> compiled = new HashMap<>();
        for (Map.Entry<String, ArrayList<V>> languageStack : stack.entrySet()) {

            compiled.put(languageStack.getKey(), compile(languageStack.getValue().stream(), mapper));
        }

        return new ImmutableMap<>(compiled);
    }

    private static <V, K> ImmutableMap<String, K> compile(Stream<V> resources, Function<V, Map<String, K>> mapper) {

        return new ImmutableMap<>(resources.map(mapper).flatMap(map -> map.entrySet().stream()).collect(Collectors
                .toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement, HashMap::new)));
    }

    private static StyleSet createStyle(Stream<ResourceSet> resources) {

        return StyleSet.createStyleSet(resources.map(ResourceSet::getStyle).toList());
    }

    // TODO: Add runtime engine here
    // All updates should be handled within the runtime; when graphics need to be
    // displayed (UI, renderInstance, etc.) share copy of data to render engine and
    // continue cycling
    // Wait for UI/render requests from render engine to avoid overloading w/ too
    // many contexts
}
