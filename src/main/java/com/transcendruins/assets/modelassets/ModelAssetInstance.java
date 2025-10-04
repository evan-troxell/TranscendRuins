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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.animationcontrollers.AnimationControllerContext;
import com.transcendruins.assets.animationcontrollers.AnimationControllerInstance;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.models.ModelAttributes;
import com.transcendruins.assets.models.ModelContext;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.rendermaterials.RenderMaterialContext;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.graphics3d.PolyGroup;
import com.transcendruins.graphics3d.geometry.Quaternion;
import com.transcendruins.graphics3d.geometry.Triangle;
import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;

/**
 * <code>ModelAssetInstance</code>: A class representing an
 * <code>AssetInstance</code> instance which has the capability of being
 * rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetInstance extends AssetInstance implements RenderInstance {

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

    /**
     * <code>ModelInstance</code>: The model of this <code>ModelAssetInstance</code>
     * instance.
     */
    private ModelInstance model;

    @Override
    public final ModelInstance getModel() {

        return model;
    }

    /**
     * <code>RenderMaterialInstance</code>: The render material of this
     * <code>ModelAssetInstance</code> instance.
     */
    private RenderMaterialInstance renderMaterial;

    @Override
    public final RenderMaterialInstance getRenderMaterial() {

        return renderMaterial;
    }

    /**
     * <code>AnimationControllerInstance</code>: The animation controller of this
     * <code>ModelAssetInstance</code> instance.
     */
    private AnimationControllerInstance animationController;

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

    @Override
    public final Color getRGB(double x, double y) {

        if (texture == null || texture.getWidth() == 0 || texture.getHeight() == 0) {

            return Texture.BLANK;
        }

        x *= (double) texture.getWidth() / model.getTextureWidth();
        y *= (double) texture.getHeight() / model.getTextureHeight();

        if (x < 0 || x > texture.getWidth() || y < 0 || y > texture.getHeight()) {

            return Texture.BLANK;
        }

        int boundedX = Math.min(texture.getWidth(), Math.max(0, (int) x));
        int boundedY = Math.min(texture.getHeight(), Math.max(0, (int) y));

        return new Color(texture.getRGB(boundedX, boundedY));
    }

    @Override
    public final BoneActorSet getBoneActors() {

        return animationController == null ? new BoneActorSet() : animationController.evaluatePose();
    }

    /**
     * Retrieves the polygon groups of this <code>ModelAssetInstance</code>
     * instance.
     * 
     * @param boneActors <code>BoneActorSet</code>: The bone actors to apply to this
     *                   <code
     * @param model      <code>ModelInstance</code>: The model of this
     *                   <code>ModelAssetInstance</code> which should be rendered.
     * @param position   <code>Vector</code>: The position at which to center the
     *                   polygons of this <code>ModelAssetInstance</code> instance.
     * @param rotation   <code>Quaternions</code>: The orientation to apply to the
     *                   polygons of this <code>ModelAssetInstance</code> instance.
     * @return
     */
    protected abstract HashMap<Triangle, Triangle> getPolygons(BoneActorSet boneActors, ModelInstance model,
            Vector position, Quaternion rotation);

    /**
     * <code>HashMap&lt;ModelAssetInstance, String&gt;</code>: The children of this
     * <code>ModelAssetInstance</code> instance. These represent pairs between child
     * assets and the bone to which they are attached.
     */
    private final HashMap<ModelAssetInstance, String> children = new HashMap<>();

    /**
     * Adds a child model to this <code>ModelAssetInstance</code> instance. The
     * child model will be attached to the bone with the specified name.
     * 
     * @param child    <code>ModelAssetInstance</code>: The child model to add to
     *                 this <code>ModelAssetInstance</code> instance.
     * @param boneName <code>String</code>: The name of the bone to which the child
     *                 model will be attached.
     * @return <code>boolean</code>: Whether or not the child was successfully
     *         added.
     */
    protected final boolean addChild(ModelAssetInstance child, String boneName) {

        // If the model does not have the bone, do not add the child.
        if (!model.hasBone(boneName)) {

            return false;
        }

        children.put(child, boneName);
        child.setParent(this);

        return true;
    }

    /**
     * Removes a child model from this <code>ModelAssetInstance</code> instance.
     * 
     * @param child <code>ModelAssetInstance</code>: The child model to remove from
     *              this <code>ModelAssetInstance</code> instance.
     */
    protected final void removeChild(ModelAssetInstance child) {

        // If the child is a child of this model, remove it.
        if (children.remove(child) != null) {

            child.setParent(null);
        }
    }

    /**
     * Retrieves the children of this <code>ModelAssetInstance</code> instance.
     * 
     * @return <code>ImmutableMap&lt;ModelAssetInstance, String&gt;</code>: The
     *         <code>children</code> field of this <code>ModelAssetInstance</code>
     *         instance.
     */
    protected final ImmutableMap<ModelAssetInstance, String> getChildren() {

        return new ImmutableMap<>(children);
    }

    @Override
    public final HashSet<PolyGroup> getPolygons() {

        return getPolygons(new ArrayList<>(), new ArrayList<>(), getPosition(), getRotation());
    }

    /**
     * Retrieves the polygons of this <code>ModelAssetInstance</code> instance and
     * its children. The polygons of children elements will not be directly
     * retrieved by the rendering system, so this method recursively compiles each
     * child element into a set of polygons to be rendered.
     * 
     * @param parentsBoneActors <code>List&lt;BoneActorSet&gt;</code>: The bone
     *                          actors of the parent model asset instances.
     * @param parents           <code>List&lt;ModelAttributes.Bone&gt;</code>: The
     *                          bones of the parent model asset instances.
     * @param position          <code>Vector</code>: The position at which to center
     *                          the polygons of this <code>ModelAssetInstance</code>
     *                          instance.
     * @param rotation          <code>Quaternion</code>: The orientation to apply to
     *                          the polygons of this <code>ModelAssetInstance</code>
     *                          instance.
     * @return <code>HashSet&lt;PolyGroup&gt;</code>: The polygons of this
     *         <code>ModelAssetInstance</code> instance and its children.
     */
    private HashSet<PolyGroup> getPolygons(List<BoneActorSet> parentsBoneActors, List<ModelAttributes.Bone> parents,
            Vector position, Quaternion rotation) {

        BoneActorSet boneActors = getBoneActors();

        // Retrieve the map of polygons to their uvs.
        HashMap<Triangle, Triangle> polygonsAdjusted = model.getPolygons(boneActors, parentsBoneActors, parents,
                position, rotation);

        // Recursively subdivides the polygon group until it is within the appropriate
        // size.
        HashSet<PolyGroup> subdividedPolygons = new PolyGroup(polygonsAdjusted, this).subDivide();

        // Extend this model asset as a parent of the children models.
        ArrayList<BoneActorSet> childParentBoneActors = new ArrayList<>(parentsBoneActors);
        childParentBoneActors.add(boneActors);

        for (Map.Entry<ModelAssetInstance, String> childEntry : children.entrySet()) {

            ModelAssetInstance child = childEntry.getKey();
            String boneName = childEntry.getValue();

            // If the bone is not found, skip this child.
            if (!model.hasBone(boneName)) {

                continue;
            }

            ModelAttributes.Bone bone = model.getBone(boneName);

            // Extend the bone as a parent of the child model.
            ArrayList<ModelAttributes.Bone> childParentBones = new ArrayList<>(parents);
            childParentBones.add(bone);

            // Retrieve the polygons of the child model asset instance.
            HashSet<PolyGroup> childPolygons = child.getPolygons(childParentBoneActors, childParentBones, position,
                    rotation);
            subdividedPolygons.addAll(childPolygons);
        }

        return subdividedPolygons;
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
            return (ModelInstance) modelContext.instantiate();
        }, model);

        // Updates the renderMaterial field.
        renderMaterial = calculateAttribute(attributes.getRenderMaterial(), val -> {

            RenderMaterialContext renderMaterialContext = new RenderMaterialContext(val, getWorld(), this);
            return (RenderMaterialInstance) renderMaterialContext.instantiate();
        }, renderMaterial);

        // Updates the animationController field.
        animationController = calculateAttribute(attributes.getAnimationController(), val -> {

            AnimationControllerContext animationControllerContext = new AnimationControllerContext(val, getWorld(),
                    this);
            return (AnimationControllerInstance) animationControllerContext.instantiate();
        }, animationController, attributes, null);

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

        if (animationController != null) {

            animationController.update(time);
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
