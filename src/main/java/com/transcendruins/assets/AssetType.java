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

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.transcendruins.App;
import com.transcendruins.assets.animations.AnimationAttributes;
import com.transcendruins.assets.animations.AnimationInstance;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.assets.layouts.LayoutAttributes;
import com.transcendruins.assets.layouts.LayoutInstance;
import com.transcendruins.assets.loottables.LootTableAttributes;
import com.transcendruins.assets.loottables.LootTableInstance;
import com.transcendruins.assets.modelassets.elements.ElementAttributes;
import com.transcendruins.assets.modelassets.elements.ElementInstance;
import com.transcendruins.assets.modelassets.entities.EntityAttributes;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.items.ItemAttributes;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.recipes.RecipeAttributes;
import com.transcendruins.assets.recipes.RecipeInstance;
import com.transcendruins.assets.rendermaterials.RenderMaterialAttributes;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.assets.statecontrollers.StateControllerAttributes;
import com.transcendruins.assets.statecontrollers.StateControllerInstance;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>AssetType</code>: An enum class representing each asset type.
 */
public enum AssetType {

    /**
     * <code>AssetType</code>: An asset type representing elements.
     */
    ELEMENT(ElementAttributes::new, ElementInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing entities.
     */
    ENTITY(EntityAttributes::new, EntityInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing state controllers.
     */
    STATE_CONTROLLER(StateControllerAttributes::new, StateControllerInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing animations.
     */
    ANIMATION(AnimationAttributes::new, AnimationInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing models.
     */
    MODEL(ModelAttributes::new, ModelInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing render materials.
     */
    RENDER_MATERIAL(RenderMaterialAttributes::new, RenderMaterialInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing items.
     */
    ITEM(ItemAttributes::new, ItemInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing loot tables.
     */
    LOOT_TABLE(LootTableAttributes::new, LootTableInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing recipes.
     */
    RECIPE(RecipeAttributes::new, RecipeInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing layouts.
     */
    LAYOUT(LayoutAttributes::new, LayoutInstance::new),

    /**
     * <code>AssetType</code>: An asset type representing interfaces.
     */
    INTERFACE(InterfaceAttributes::new, InterfaceInstance::new);

    /**
     * Maps each of the asset types to an associated icon.
     * 
     * @param <K>:   The value type to map.
     * @param mapper <code>Function&lt;AssetType, K&gt;</code>: The mapping function
     *               to use.
     * @return <code>HashMap&lt;AssetType, K&gt;</code>: The generated asset map.
     */
    public static final <K> HashMap<AssetType, K> createAssetMap(Function<AssetType, K> mapper) {

        Stream<AssetType> assets = Arrays.stream(values());
        HashMap<AssetType, K> map = assets
                .collect(Collectors.toMap(type -> type, mapper::apply, (value, _) -> value, HashMap::new));

        return map;
    }

    /**
     * <code>SchemaAttributeCreater</code>: The schema creater of this
     * <code>AssetType</code> instance.
     */
    private final SchemaAttributeCreater attributeCreater;

    /**
     * <code>SchemaAttributeCreater</code>: A functional interface representing the
     * constructor for a schema attribute set.
     */
    @FunctionalInterface
    private interface SchemaAttributeCreater {

        /**
         * Creates a new asset schema attribute set.
         * 
         * @param schema     <code>AssetSchema</code>: The schema to create the
         *                   attribute set using.
         * @param jsonSchema <code>TracedDictionary</code>: The JSON dictionary to
         *                   parse.
         * @param isBase     <code>boolean</code>: Whether or not the attribute set is
         *                   the base set.
         * @return <code>AssetSchema</code>: The constructed attribute set.
         * @throws LoggedException Thrown if any exception is raised while creating the
         *                         schema attributes.
         */
        AssetAttributes apply(AssetSchema schema, TracedDictionary jsonSchema, boolean isBase) throws LoggedException;
    }

    /**
     * Creates a new attribute set from the <code>attributeCreater</code> field of
     * this <code>AssetType</code> instance.
     * 
     * @param schema     <code>AssetSchema</code>: The schema to create the
     *                   attribute set using.
     * @param jsonSchema <code>TracedDictionary</code>: The JSON dictionary to
     *                   parse.
     * @param isBase     <code>boolean</code>: Whether or not the attribute set is
     *                   the base set.
     * @return <code>AssetAttributes</code>: The constructed attribute set.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         schema attributes.
     */
    public final AssetAttributes createAttributes(AssetSchema schema, TracedDictionary jsonSchema, boolean isBase)
            throws LoggedException {

        return attributeCreater.apply(schema, jsonSchema, isBase);
    }

    /**
     * Creates a new asset schema from this <code>AssetType</code> instance.
     * 
     * @param path <code>AssetSchema</code>: The path to the schema.
     * @return <code>AssetSchema</code>: The constructed schema.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         schema.
     */
    public final AssetSchema createSchema(TracedPath path) throws LoggedException {

        return new AssetSchema(path, this);
    }

    /**
     * <code>Object</code>: The asset instantiation key which each asset requires to
     * be created. This key is only present within the <code>AssetType</code> class
     * where the asset is guaranteed to be initialized properly, which is why asset
     * types are responsible for the creation of new assets.
     */
    public static final Object KEY = new Object();

    /**
     * <code>InstanceCreater</code>: The asset instancer creater of this
     * <code>AssetType</code> instance.
     */
    private final InstanceCreater instanceCreater;

    /**
     * <code>InstanceCreater</code>: A functional interface representing the
     * constructor for an asset instance.
     */
    @FunctionalInterface
    private interface InstanceCreater {

        /**
         * Creates a new asset instance.
         * 
         * @param context <code>AssetContext</code>: The context with which to create
         *                the new asset.
         * @param key     <code>Object</code>: The key used to create the new asset.
         * @return <code>AssetInstance</code>: The constructed asset instance.
         */
        AssetInstance apply(AssetContext assetContext, Object key);
    }

    /**
     * Creates and initializes a new asset of the type of this
     * <code>AssetType</code> instance.
     * 
     * @param assetContext <code>AssetContext</code>: The context from which to
     *                     create the new asset.
     * @return <code>AssetInstance</code>: The generated (and initialized) asset.
     */
    public final AssetInstance createAsset(AssetContext assetContext) {

        AssetInstance asset = instanceCreater.apply(assetContext, KEY);
        asset.initialize();

        return asset;
    }

    /**
     * <code>String</code>: The camel-case version of this <code>AssetType</code>
     * instance.
     */
    private final String name;

    /**
     * <code>String>/code>: The sentence-case version of this <code>AssetType</code>
     * instance.
     */
    private final String sentenceName;

    /**
     * Creates a new instance of the <code>AssetType</code> enum class.
     * 
     * @param attributeCreater <code>SchemaAttributeCreater</code>: The schema
     *                         creater of this <code>AssetType</code> instance.
     */
    private AssetType(SchemaAttributeCreater attributeCreater, InstanceCreater instanceCreater) {

        this.attributeCreater = attributeCreater;
        this.instanceCreater = instanceCreater;

        name = App.toCamelCase(name());
        sentenceName = App.toSentenceCase(name());
    }

    /**
     * Returns the camel-case string version of this <code>AssetType</code>
     * instance.
     * 
     * @return <code>String</code>: The camel-case version of this
     *         <code>AssetType</code> instance.
     */
    @Override
    public final String toString() {

        return name;
    }

    public final String toSentenceString() {

        return sentenceName;
    }
}
