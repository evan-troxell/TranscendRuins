/* Copyright 2026 Evan Troxell
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

import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.catalogue.AssetCatalogue;
import com.transcendruins.assets.catalogue.AssetCatalogue.PinIcon;
import com.transcendruins.assets.catalogue.RecipeSet;
import com.transcendruins.assets.catalogue.events.GlobalEventInstance;
import com.transcendruins.assets.catalogue.events.GlobalEventSchema;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.catalogue.locations.GlobalLocationSchema;
import com.transcendruins.assets.catalogue.locations.LocationTriggerType;
import com.transcendruins.assets.interfaces.map.LocationRender;
import com.transcendruins.assets.interfaces.map.TerrainRender;
import com.transcendruins.assets.modelassets.entities.EntityContext;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.recipes.RecipeContext;
import com.transcendruins.assets.recipes.RecipeInstance;
import com.transcendruins.geometry.Vector;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.rendering.renderbuffer.RenderBuffer;
import com.transcendruins.resources.ResourceSet;
import com.transcendruins.resources.languages.Language;
import com.transcendruins.resources.sounds.Sound;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.utilities.PropertyHolder;
import com.transcendruins.utilities.files.DataConstants;
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

    public enum LanguageType {

        ENGLISH("en");

        private final String key;

        public final String getKey() {

            return key;
        }

        LanguageType(String key) {

            this.key = key;
        }
    }

    /**
     * <code>int</code>: The length and width of a unit tile.
     */
    public static final int UNIT_TILE = 24;

    /**
     * <code>Vector</code>: The 3D bounds of a unit tile.
     */
    public static final Vector UNIT_TILE_VECTOR = new Vector(UNIT_TILE, UNIT_TILE, UNIT_TILE);

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>East</code>.
     */
    public static final int EAST = 0;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>North</code>.
     */
    public static final int NORTH = 1;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>West</code>.
     */
    public static final int WEST = 2;

    /**
     * <code>int</code>: An enum constant representing the cardinal direction
     * <code>South</code>.
     */
    public static final int SOUTH = 3;

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
     * Retreives the random number generater of this <code>World</code> instance.
     * 
     * @return <code>DeterministicRandom</code>: The <code>random</code> field of
     *         this <code>World</code> instance.
     */
    public final DeterministicRandom getRandom() {

        return random;
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

    private ImmutableMap<String, PinIcon> pinIcons;

    public final PinIcon getPinIcon(String pin) {

        return pinIcons.get(pin);
    }

    private String defaultLocation;

    private ImmutableMap<String, GlobalLocationSchema> locationSchemas;

    private ImmutableList<TerrainRender> globalTerrain;

    private ImmutableMap<String, ImmutableList<GlobalEventSchema>> eventSchemas;

    private ImmutableMap<String, AssetPresets> overlays;

    private ImmutableMap<String, AssetPresets> globalOverlays;

    private ImmutableMap<String, AssetPresets> menus;

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
        return context.instantiate();
    }

    /**
     * <code>String</code>: The current language of this <code>World</code>
     * instance.
     */
    private String language;

    /**
     * Sets the current language of this <code>World</code> instance.
     * 
     * @param language <code>LanguageType</code>: The language to apply to this
     *                 <code>World</code> instance.
     */
    public final void setLanguage(LanguageType language) {

        this.language = language.getKey();
    }

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

            return DataConstants.MISSING_SOUND.retrieveAudio();
        }

        StoredSound audio = sounds.get(sound).getSound(random, soundPaths);
        return audio != null ? audio : DataConstants.MISSING_SOUND.retrieveAudio();
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

            return DataConstants.MISSING_TEXTURE.retrieveImage();
        }

        ImageIcon icon = textures.get(texture).getTexture(random, texturePaths);
        return icon != null ? icon : DataConstants.MISSING_TEXTURE.retrieveImage();
    }

    private StyleSet style;

    public final StyleSet getStyle() {

        return style;
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

        // Join the content pack resources to the resource pack resources and apply.
        List<ResourceSet> resourceList = new ArrayList<>();
        resourceList.addAll(packs.stream().map(ContentPack::getResources).toList());
        resourceList.addAll(resources.stream().map(ResourcePack::getResources).toList());
        applyResources(resourceList);

        // Apply the catalogues.
        List<AssetCatalogue> catalogues = packs.stream().map(ContentPack::getCatalogue).toList();
        applyCatalogue(catalogues);
    }

    public final void applyResources(List<ResourceSet> resources) {

        languages = new ImmutableMap<>(
                compileGroup(resources, set -> set.getLanguages().getLanguages(), Language::getMappings));

        sounds = compile(resources, set -> set.getSounds().getSounds());
        soundPaths = compile(resources, set -> set.getSounds().getPaths());

        textures = compile(resources, set -> set.getTextures().getTextures());
        texturePaths = compile(resources, set -> set.getTextures().getPaths());

        style = createStyle(resources.stream());
    }

    public final void applyCatalogue(List<AssetCatalogue> catalogues) {

        pinIcons = compile(catalogues, AssetCatalogue::getPinIcons);

        defaultLocation = null;
        for (int i = catalogues.size() - 1; i >= 0; i--) {

            defaultLocation = catalogues.get(i).getDefaultLocation();
            if (defaultLocation != null) {

                break;
            }
        }

        locationSchemas = compile(catalogues, AssetCatalogue::getLocations);
        queuedLocations.addAll(locationSchemas.entrySet().stream()
                .filter(entry -> entry.getValue().getTriggerType() == LocationTriggerType.AUTOMATIC)
                .map(Map.Entry::getKey).toList());

        // Create all of the locations that do not have an implicit start time.
        List<String> newLocations = queuedLocations.stream()
                .filter(location -> locationSchemas.get(location).getDuration().getStartTimestamp() == null).toList();
        queuedLocations.removeAll(newLocations);

        if (!newLocations.isEmpty()) {

            synchronized (LOCATION_LOCK) {

                for (String location : newLocations) {

                    GlobalLocationSchema schema = locationSchemas.get(location);
                    GlobalLocationInstance instance = new GlobalLocationInstance(schema, this, location);
                    locations.put(location, instance);
                }
            }
        }

        globalTerrain = new ImmutableList<>(
                catalogues.stream().flatMap(catalogue -> catalogue.getTerrain().stream()).toList());

        eventSchemas = compile(catalogues, AssetCatalogue::getEvents);

        overlays = compile(catalogues, AssetCatalogue::getOverlays);
        globalOverlays = compile(catalogues, AssetCatalogue::getGlobalOverlays);
        menus = compile(catalogues, AssetCatalogue::getMenus);

        recipes = compileGroup(catalogues, AssetCatalogue::getRecipes, RecipeSet::getRecipes);
    }

    public static final <T, V, K> ImmutableMap<String, ImmutableMap<String, K>> compileGroup(List<T> resources,
            Function<T, Map<String, V>> retriever, Function<V, Map<String, K>> mapper) {

        HashMap<String, ArrayList<V>> stack = new HashMap<>();
        resources.stream().map(retriever).forEach(set -> {

            for (Map.Entry<String, V> mapEntry : set.entrySet()) {

                ArrayList<V> group = stack.computeIfAbsent(mapEntry.getKey(), _ -> new ArrayList<>());
                group.add(mapEntry.getValue());
            }
        });

        HashMap<String, ImmutableMap<String, K>> compiled = new HashMap<>();
        for (Map.Entry<String, ArrayList<V>> languageStack : stack.entrySet()) {

            compiled.put(languageStack.getKey(), compile(languageStack.getValue(), mapper));
        }

        return new ImmutableMap<>(compiled);
    }

    private static <V, K> ImmutableMap<String, K> compile(List<V> resources, Function<V, Map<String, K>> mapper) {

        return new ImmutableMap<>(
                resources.stream().map(mapper).flatMap(map -> map.entrySet().stream()).collect(Collectors
                        .toMap(Map.Entry::getKey, Map.Entry::getValue, (_, replacement) -> replacement, HashMap::new)));
    }

    private static StyleSet createStyle(Stream<ResourceSet> resources) {

        return StyleSet.createStyleSet(resources.map(ResourceSet::getStyle).toList());
    }

    private final Object FRAMERATE_LOCK = new Object();

    private long simulationRate = 40;
    private long tickNs = simulationRate > 0 ? 1_000_000_000l / simulationRate : 0;

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

        return active ? System.currentTimeMillis() - timeOfCreation : 0;
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

    private final HashSet<String> queuedLocations = new HashSet<>();

    private final LinkedHashMap<String, GlobalLocationInstance> locations = new LinkedHashMap<>();

    public final GlobalLocationInstance getLocation(String location) {

        synchronized (LOCATION_LOCK) {

            return locations.get(location);
        }
    }

    public final Map<String, LocationRender> getLocationRenders() {

        synchronized (LOCATION_LOCK) {

            return locations.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getRender()));
        }
    }

    private final Object LOCATION_LOCK = new Object();

    public final List<TerrainRender> getTerrainRenders() {

        return globalTerrain;
    }

    private final LinkedHashMap<String, GlobalEventInstance> events = new LinkedHashMap<>();

    private final HashMap<Long, Player> players = new HashMap<>();

    private final Object PLAYER_LOCK = new Object();

    public final boolean addPlayer(long playerId) {

        return addPlayer(playerId, defaultLocation);
    }

    public final boolean addPlayer(long playerId, String location) {

        // If there is already a player with the same id, do not add.
        synchronized (PLAYER_LOCK) {

            if (players.containsKey(playerId)) {

                return false;
            }
        }

        GlobalLocationInstance locationInstance;
        synchronized (LOCATION_LOCK) {

            location = locations.containsKey(location) ? location : defaultLocation;
            locationInstance = locations.get(location);
        }

        if (locationInstance == null) {

            return false;
        }

        EntityContext playerContext = new EntityContext(DataConstants.PLAYER_IDENTIFIER, locationInstance);
        EntityInstance playerEntity = playerContext.instantiate();
        Player player = new Player(playerId, playerEntity);

        boolean added = locationInstance.add(player, null);
        if (!added) {

            return false;
        }

        player.setGlobalMapCoordinates(locationInstance.getCoordinates());
        synchronized (PLAYER_LOCK) {

            players.put(playerId, player);
        }

        player.setLocation(location);

        if (!location.equals(defaultLocation)) {

            enterLocation(player, locationInstance);
        } else {

            exitLocation(playerId);
        }

        return true;
    }

    public final boolean travel(long playerId, String location) {

        return playerFunction(playerId, player -> travel(player, location, null));
    }

    public final boolean travel(Player player, String location, PlayerSpawn spawn) {

        synchronized (LOCATION_LOCK) {

            // If a location is not specified, assume the current location.
            if (location == null) {

                location = player.getLocation();

                // If neither the current nor previous location exists, halt.
                if (!locations.containsKey(location)) {

                    if (location != null) {

                        player.setLocation(null);
                    }
                    return false;
                }
            } else {

                // If the new location does not exist, halt.
                if (!locations.containsKey(location)) {

                    return false;
                }
            }

            // If the current location is new, exit the old location.
            String prevLocation = player.getLocation();
            if (!location.equals(prevLocation)) {

                if (prevLocation != null) {

                    locations.get(prevLocation).remove(player);
                }

                player.setLocation(location);
            }

            GlobalLocationInstance locationInstance = locations.get(location);
            if (locationInstance.add(player, spawn)) {

                player.setGlobalMapCoordinates(locationInstance.getCoordinates());
                return true;
            }

            return false;
        }
    }

    public final void enterLocation(long playerId) {

        playerConsumer(playerId, player -> {

            GlobalLocationInstance location;
            synchronized (LOCATION_LOCK) {

                String locationKey = player.getLocation();
                if (locationKey == null || !locations.containsKey(locationKey)) {

                    return;
                }
                location = locations.get(locationKey);
            }

            enterLocation(player, location);
        });
    }

    public final void enterLocation(Player player, GlobalLocationInstance location) {

        if (!location.enter(player)) {

            // If the player could not enter the location, do not proceed.
            return;
        }

        player.exitGlobalMap();
        player.setPanels(overlays.values());
    }

    public final void exitLocation(long playerId) {

        playerConsumer(playerId, player -> {

            synchronized (LOCATION_LOCK) {

                String locationKey = player.getLocation();
                if (locationKey != null && locations.containsKey(locationKey)) {

                    GlobalLocationInstance location = locations.get(locationKey);
                    location.exit(player);
                }
            }

            player.enterGlobalMap();
            player.setPanels(globalOverlays.values());
        });
    }

    public final void setScreenSize(long playerId, int width, int height) {

        playerConsumer(playerId, player -> player.setScreenSize(width, height));
    }

    public final void setMousePosition(long playerId, int x, int y) {

        playerConsumer(playerId, player -> player.setMousePosition(x, y));
    }

    public final BufferedImage renderUi(long playerId) {

        return playerFunction(playerId, Player::renderUi);
    }

    public final void openMenu(long playerId, String menu) {

        if (!menus.containsKey(menu)) {

            return;
        }
        List<AssetPresets> panel = List.of(menus.get(menu));

        playerConsumer(playerId, player -> {

            player.setPanels(panel);
        });
    }

    public final void closeMenu(long playerId) {

        playerConsumer(playerId, player -> {

            if (player.onGlobalMap()) {

                player.setPanels(globalOverlays.values());
            } else {

                player.setPanels(overlays.values());
            }
        });
    }

    public final ItemInstance consumeSlot(long playerId, int slot) {

        return playerFunction(playerId, player -> player.getEntity().getInventory().getSlot(slot).putItem(null));
    }

    public final ItemInstance consumeSlot(long playerId, String slot) {

        return playerFunction(playerId, player -> player.getEntity().getInventory().getSlot(slot).putItem(null));
    }

    public final int getItemCount(long playerId, ItemInstance item) {

        return playerFunction(playerId, player -> player.getEntity().getInventory().getItemCount(item));
    }

    public final int consumeItem(long playerId, ItemInstance item) {

        return playerFunction(playerId, player -> player.getEntity().getInventory().consume(item));
    }

    public final RenderBuffer getPolygons(long playerId) {

        return playerFunction(playerId, player -> getLocation(player.getLocation()).getArea(player).getPolygons());
    }

    public final <K> K playerFunction(long playerId, Function<Player, K> operator) {

        Player player;

        synchronized (PLAYER_LOCK) {

            player = players.get(playerId);
        }

        if (player == null) {

            return null;
        }

        return operator.apply(player);
    }

    public final void playerConsumer(long playerId, Consumer<Player> operator) {

        Player player;

        synchronized (PLAYER_LOCK) {

            player = players.get(playerId);
        }

        if (player == null) {

            return;
        }

        operator.accept(player);
    }

    private boolean active = false;

    private Thread host = null;

    public final synchronized void startHost() {

        timeOfCreation = System.currentTimeMillis();
        active = true;

        host = new Thread(this::host);
        host.start();
    }

    public final synchronized void endHost() {

        active = false;
        host = null;
    }

    private void host() {

        int frame = 0;

        while (true) {

            long start = System.nanoTime();
            long startMs = System.currentTimeMillis();

            synchronized (this) {

                if (!active) {

                    break;
                }

                long time = System.currentTimeMillis();
                double runtime = getRuntimeSeconds();

                ZonedDateTime now = ZonedDateTime.now();

                List<String> expiredLocations = locations.entrySet().stream()
                        .filter(entry -> entry.getValue().expired(now)).map(Map.Entry::getKey).toList();
                locations.keySet().removeAll(expiredLocations);

                List<String> newLocations = queuedLocations.stream().filter(
                        location -> !now.isBefore(locationSchemas.get(location).getDuration().getStartTimestamp()))
                        .toList();
                queuedLocations.removeAll(newLocations);

                for (String location : newLocations) {

                    GlobalLocationSchema schema = locationSchemas.get(location);
                    GlobalLocationInstance instance = new GlobalLocationInstance(schema, this, location);
                    locations.put(location, instance);
                }

                // Retrieve the active locations.
                Set<String> activeLocations = players.values().stream().map(Player::getLocation)
                        .collect(Collectors.toSet());

                // Update the active locations.
                for (String location : activeLocations) {

                    locations.get(location).update(runtime);
                }

                // Update the UIs and recompute interacts.
                for (Player player : players.values()) {

                    player.updateUiPanels(runtime);

                    String playerLocation = player.getLocation();
                    boolean inLocation = !player.onGlobalMap() && locations.containsKey(playerLocation);

                    if (inLocation) {

                        GlobalLocationInstance location = locations.get(playerLocation);
                        AreaGrid area = location.getArea(player);

                        player.setInteraction(area.getNearestInteraction(player, time));
                        player.setAttack(area.getNearestTarget(player.getEntity()));
                    } else {

                        player.setInteraction(null);
                        player.setAttack(null);
                    }
                }
            }

            synchronized (FRAMERATE_LOCK) {

                if (simulationRate > 0) {

                    while (tickNs + start - System.nanoTime() > 0) {
                    }
                }
            }

            // System.out.println("Frame " + frame++ + " : " + (System.currentTimeMillis() -
            // startMs) + "ms");
        }
    }

    public static final class StopWatch {

        private long start = -1;

        public final void start() {

            start = System.currentTimeMillis();
        }

        public final void stop(Object message) {

            if (start == -1) {

                return;
            }

            long end = System.currentTimeMillis();
            System.out.println(message + " : " + (end - start));
            start = -1;
        }
    }
}
