package com.transcendruins.packcompiling.assetschemas;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.world.assetinstances.animationcontrollers.AnimationControllerPresets;
import com.transcendruins.world.assetinstances.models.ModelPresets;
import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialPresets;

/**
 * <code>ModelAssetSchemaAttributes</code>: A class which represents the
 * attributes of an <code>AssetSchema</code> instance which has the capability
 * of being rendered using the standard <code>RenderInstance</code> method.
 */
public abstract class ModelAssetSchemaAttributes extends AssetSchemaAttributes {

    /**
     * <code>ModelPresets</code>: The model presets of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final ModelPresets model;

    /**
     * Retrieves the model presets of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>ModelPresets</code>: The <code>model</code> field of
     *         this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final ModelPresets getModel() {

        return model;
    }

    /**
     * <code>RenderMaterialPresets</code>: The render material presets of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final RenderMaterialPresets renderMaterial;

    /**
     * Retrieves the render material presets of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>RenderMaterialPresets</code>: The <code>renderMaterial</code>
     *         field of this <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final RenderMaterialPresets getRenderMaterial() {

        return renderMaterial;
    }

    /**
     * <code>AnimationControllerPresets</code>: The animation controller presets of
     * this
     * <code>ModelAssetSchemaAttributes</code> instance.
     */
    private final AnimationControllerPresets animationController;

    /**
     * Retrieves the animation controller presets of this
     * <code>ModelAssetSchemaAttributes</code> instance.
     * 
     * @return <code>AnimationControllerPresets</code>: The
     *         <code>animationController</code> field of this
     *         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public final AnimationControllerPresets getAnimationController() {

        return animationController;
    }

    /**
     * Compiles this <code>ModelAssetSchemaAttributes</code> instance into a
     * completed instance.
     * 
     * @param schema <code>AssetSchema</code>: The schema which created this
     *               <code>ModelAssetSchemaAttributes</code> instance.
     * @param json   <code>TracedDictionary</code>: The schema JSON used to
     *               compile this <code>ModelAssetSchemaAttributes</code>
     *               instance.
     * @param isBase <code>boolean</code>: Whether or not this
     *               <code>ModelAssetSchemaAttributes</code> instance is the
     *               base attribute set of an <code>AssetSchema</code> instance.
     * @throws LoggedException Thrown if an exception is raised while creating this
     *                         <code>ModelAssetSchemaAttributes</code> instance.
     */
    public ModelAssetSchemaAttributes(AssetSchema schema, TracedDictionary json, boolean isBase)
            throws LoggedException {

        super(schema, json, isBase);

        model = ModelPresets.createPresets(json, "model", !isBase);
        if (model != null) {

            addElementDependency(model);
        }

        renderMaterial = RenderMaterialPresets.createPresets(json, "renderMaterial", !isBase);
        if (renderMaterial != null) {

            addElementDependency(renderMaterial);
        }

        animationController = AnimationControllerPresets.createPresets(json, "animationController", true);
        if (animationController != null) {

            addElementDependency(animationController);
        }
    }
}
