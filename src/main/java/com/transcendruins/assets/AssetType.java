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

import com.transcendruins.assets.animationcontrollers.AnimationControllerAttributes;
import com.transcendruins.assets.animations.AnimationAttributes;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.assets.schema.AssetSchema;
import com.transcendruins.assets.layouts.LayoutAttributes;
import com.transcendruins.assets.interfaces.InterfaceAttributes;
import com.transcendruins.assets.loottables.LootTableAttributes;
import com.transcendruins.assets.modelassets.elements.ElementAttributes;
import com.transcendruins.assets.modelassets.entities.EntityAttributes;
import com.transcendruins.assets.modelassets.items.ItemAttributes;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.recipes.RecipeAttributes;
import com.transcendruins.assets.rendermaterials.RenderMaterialAttributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.TracedDictionary;

/**
 * <code>AssetType</code>: An enum class representing each asset type.
 */
public enum AssetType {

    /**
     * <code>AssetType</code>: An enum representing animation controllers.
     */
    ANIMATION_CONTROLLER(AnimationControllerAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing animations.
     */
    ANIMATION(AnimationAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing elements.
     */
    ELEMENT(ElementAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing entities.
     */
    ENTITY(EntityAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing items.
     */
    ITEM(ItemAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing models.
     */
    MODEL(ModelAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing render materials.
     */
    RENDER_MATERIAL(RenderMaterialAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing layouts.
     */
    LAYOUT(LayoutAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing interfaces.
     */
    INTERFACE(InterfaceAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing loot tables.
     */
    LOOT_TABLE(LootTableAttributes::new),

    /**
     * <code>AssetType</code>: An enum representing recipes.
     */
    RECIPE(RecipeAttributes::new);

    /**
     * Maps each of the asset types to an associated icon.
     * 
     * @param <K>:   The value type to map.
     * @param mapper <code>Function&lt;AssetType, K&gt;</code>: The mapping function
     *               to use.
     * @return <code>HashMap&lt;AssetType, K&gt;</code>: The generated asset map.
     */
    public static final <K> HashMap<AssetType, K> buildAssetMap(Function<AssetType, K> mapper) {

        return Arrays.stream(values()).collect(Collectors.toMap(
                type -> type,
                mapper::apply,
                (value, _) -> value,
                HashMap::new));
    }

    /**
     * <code>SchemaAttributeBuilder</code>: A functional interface representing the
     * constructor for a schema attribute set
     */
    @FunctionalInterface
    interface SchemaAttributeBuilder {

        /**
         * Builds a new asset schema attribute set.
         * 
         * @param schema     <code>AssetSchema</code>: The schema to build the attribute
         *                   set using.
         * @param jsonSchema <code>TracedDictionary</code>: The JSON dictionary to
         *                   parse.
         * @param isBase     <code>boolean</code>: Whether or not the attribute set is
         *                   the base set.
         * @return <code>AssetSchema</code>: The constructed attribute set.
         * @throws LoggedException Thrown if any exception is raised while building the
         *                         schema attributes.
         */
        AssetAttributes apply(AssetSchema schema, TracedDictionary jsonSchema, boolean isBase)
                throws LoggedException;
    }

    /**
     * <code>SchemaAttributeBuilder</code>: The schema builder of this
     * <code>AssetType</code>
     * instance.
     */
    private final SchemaAttributeBuilder attributeBuilder;

    /**
     * Creates a new attribute set from the <code>attributeBuilder</code> field of
     * this <code>AssetType</code> instance.
     * 
     * @param schema     <code>AssetSchema</code>: The schema to build the attribute
     *                   set using.
     * @param jsonSchema <code>TracedDictionary</code>: The JSON dictionary to
     *                   parse.
     * @param isBase     <code>boolean</code>: Whether or not the attribute set is
     *                   the base set.
     * @return <code>AssetSchema</code>: The constructed attribute set.
     * @throws LoggedException Thrown if any exception is raised while building the
     *                         schema attributes.
     */
    public final AssetAttributes buildAttributes(AssetSchema schema, TracedDictionary jsonSchema,
            boolean isBase)
            throws LoggedException {

        return attributeBuilder.apply(schema, jsonSchema, isBase);
    }

    /**
     * <code>String</code>: The camel-case version of this <code>AssetType</code>
     * instance.
     */
    private final String name;

    /**
     * Creates a new instance of the <code>AssetType</code> enum class.
     * 
     * @param attributeBuilder <code>SchemaAttributeBuilder</code>: The schema
     *                         builder of this x<code>AssetType</code> instance.
     */
    private AssetType(SchemaAttributeBuilder attributeBuilder) {

        this.attributeBuilder = attributeBuilder;

        String[] tokens = name().toLowerCase().split("_");
        name = tokens[0] + Arrays.stream(tokens, 1, tokens.length).map(
                token -> {

                    return Character.toUpperCase(token.charAt(0)) + token.substring(1);
                }).collect(Collectors.joining());
    }

    public final AssetSchema buildSchema(TracedPath path) throws LoggedException {

        return new AssetSchema(path, this);
    }

    /**
     * Returns the camel-case string version of this <code>AssetType</code>
     * instance.
     * 
     * @return <code>String</code>: The camel-case version of this
     *         <code>AssetType</code> instance.
     */
    @Override
    public String toString() {

        return name;
    }
}
