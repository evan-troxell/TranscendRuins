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

package com.transcendruins.assets.modelassets;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.models.ModelAttributes.WeightedVertex;
import com.transcendruins.assets.models.ModelContext;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.rendermaterials.RenderMaterialContext;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.assets.statecontrollers.StateControllerContext;
import com.transcendruins.assets.statecontrollers.StateControllerInstance;
import com.transcendruins.geometry.Quaternion;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.rendering.renderBuffer.LightData;
import com.transcendruins.rendering.renderBuffer.RenderBuffer;
import com.transcendruins.utilities.immutable.ImmutableList;

/**
 * <code>ModelAssetInstance</code>: A class representing an
 * <code>AssetInstance</code> instance which has the capability of being
 * rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetInstance extends AssetInstance implements RenderInstance {

    private GlobalLocationInstance location;

    public final GlobalLocationInstance getLocation() {

        return location;
    }

    public final void setLocation(GlobalLocationInstance location) {

        this.location = location;
        if (!hasModelParent()) {

            setParent(location);
        }
    }

    private PrimaryAssetInstance modelParent;

    protected final void setModelParent(PrimaryAssetInstance modelParent) {

        this.modelParent = modelParent;
        if (hasModelParent()) {

            location = modelParent.getLocation();
            setParent(modelParent);
        } else {

            setParent(location);
        }
    }

    public abstract void removeModelParent();

    public final boolean hasModelParent() {

        return modelParent != null;
    }

    public final PrimaryAssetInstance getModelParent() {

        return modelParent;
    }

    public abstract String getModelParentAttachment();

    /**
     * <code>String</code>: The pathway to the texture of this
     * <code>ModelAssetInstance</code> instance.
     */
    private String texturePath;

    /**
     * <code>BufferedImage</code>: The texture of this
     * <code>ModelAssetInstance</code> instance.
     */
    private BufferedImage texture;

    public final BufferedImage getTexture() {

        return texture;
    }

    /**
     * <code>ModelInstance</code>: The model of this <code>ModelAssetInstance</code>
     * instance.
     */
    private ModelInstance model;

    public final boolean containsBone(String bone) {

        return model.containsBone(bone);
    }

    /**
     * <code>RenderMaterialInstance</code>: The render material of this
     * <code>ModelAssetInstance</code> instance.
     */
    private RenderMaterialInstance renderMaterial;

    /**
     * <code>StateControllerInstance</code>: The animation controller of this
     * <code>ModelAssetInstance</code> instance.
     */
    private StateControllerInstance stateController;

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The asset category types of this
     * <code>ModelAssetInstance</code> instance.
     */
    private ImmutableList<String> categories;

    /**
     * Retrieves the asset category types of this <code>ModelAssetInstance</code>
     * instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: The <code>categories</code>
     *         field of this <code>ModelAssetInstance</code> instance.
     */
    public final ImmutableList<String> getCategories() {

        return categories;
    }

    private BoneActorSet boneActors;

    private void computeBoneActors() {

        if (stateController == null) {

            boneActors = new BoneActorSet();
        } else {

            boneActors = stateController.evaluatePose();
        }
    }

    public final BoneActorSet getBoneActors() {

        return boneActors;
    }

    @Override
    public final RenderBuffer getPolygons() {

        computeBoneActors();
        return getParentPolygons(model, boneActors, getPosition(), getRotation());
    }

    public final RenderBuffer getPolygons(ModelAssetInstance parent) {

        computeBoneActors();
        return getChildPolygons(model, boneActors, modelParent, getModelParentAttachment());
    }

    protected abstract RenderBuffer getParentPolygons(ModelInstance model, BoneActorSet boneActors, Vector position,
            Quaternion rotation);

    protected abstract RenderBuffer getChildPolygons(ModelInstance model, BoneActorSet boneActors,
            ModelAssetInstance parent, String attachment);

    protected final RenderBuffer generatePolygons(Map<Integer, Map<Vector, Double>> vertexWeights) {

        // Determine which vertices should be hidden.
        HashSet<Integer> disabledVertices = model.getDisabledVertices();

        // Form the vertices which describe the final mesh.
        List<Vector> vertices = WeightedVertex.getWeightedVertices(model.getVertices(), vertexWeights);

        // Form the polygon indices which describe the final mesh.
        List<Integer> polygons = model.getPolygons();
        ArrayList<Integer> indices = new ArrayList<>(polygons.size());

        // Collect the render-ready polygons and filter out any which are hidden.
        for (int i = 0; i < polygons.size() - 2; i += 3) {

            int v1 = polygons.get(i);
            if (disabledVertices.contains(v1)) {

                continue;
            }

            int v2 = polygons.get(i + 1);
            if (disabledVertices.contains(v2)) {

                continue;
            }

            int v3 = polygons.get(i + 2);
            if (disabledVertices.contains(v3)) {

                continue;
            }

            indices.add(v1);
            indices.add(v2);
            indices.add(v3);
        }

        List<LightData> lights = model.getLights().stream().filter(light -> !disabledVertices.contains(light.index()))
                .toList();

        return new RenderBuffer(vertices, model.getUvs(), indices, texture, model.getTextureWidth(),
                model.getTextureHeight(), renderMaterial, lights);
    }

    /**
     * Creates a new instance of the <code>ModelAssetInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>ModelAssetInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public ModelAssetInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        ModelAssetContext context = (ModelAssetContext) assetContext;

        setLocation(context.getLocation());
    }

    @Override
    public final void applyAttributes(Attributes attributeSet) {

        ModelAssetAttributes attributes = (ModelAssetAttributes) attributeSet;

        // Updates the texture field.
        texturePath = calculateAttribute(attributes.getTexture(), val -> {

            texture = getInstanceTextureAsBufferedImage(val, BufferedImage.TYPE_INT_ARGB);

            return val;
        }, texturePath);
        setProperty("texture", texturePath);

        // Updates the model field.
        model = calculateAttribute(attributes.getModel(), val -> {

            ModelContext modelContext = new ModelContext(val, getWorld(), this);
            return modelContext.instantiate();
        }, model);

        // Updates the renderMaterial field.
        renderMaterial = calculateAttribute(attributes.getRenderMaterial(), val -> {

            RenderMaterialContext renderMaterialContext = new RenderMaterialContext(val, getWorld(), this);
            return renderMaterialContext.instantiate();
        }, renderMaterial);

        // Updates the stateController field.
        stateController = calculateAttribute(attributes.getStateController(), val -> {

            StateControllerContext stateControllerContext = new StateControllerContext(val, getWorld(), this);
            return stateControllerContext.instantiate();
        }, stateController, attributes, null);

        categories = calculateAttribute(attributes.getCategories(), categories, attributes, new ImmutableList<>());
        setProperty("categories", categories);

        applyModelAssetAttributes(attributes);
    }

    /**
     * Applies an attribute set to this <code>ModelAssetInstance</code> instance.
     * 
     * @param attributeSet <code>ModelAssetAttributes</code>: The attributes to
     *                     apply.
     */
    public abstract void applyModelAssetAttributes(ModelAssetAttributes attributeSet);

    @Override
    protected final void onUpdate(double time) {

        if (stateController != null) {

            stateController.update(time);
        }

        model.update(time);
        renderMaterial.update(time);

        onModelAssetUpdate(time);
    }

    /**
     * Performs the instance update of the class of this
     * <code>ModelAssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    protected abstract void onModelAssetUpdate(double time);

    /**
     * Retrieves the position of this <code>ModelAssetInstance</code> instance.
     * 
     * @return <code>Vector</code>: The vector form of the position of this
     *         <code>ModelAssetInstance</code> instance.
     */
    public abstract Vector getPosition();

    /**
     * Retrieves the rotation of this <code>ModelAssetInstance</code> instance.
     * 
     * @return <code>Quaternion</code>: The quaternion form of the rotation of this
     *         <code>ModelAssetInstance</code> instance.
     */
    public abstract Quaternion getRotation();
}
