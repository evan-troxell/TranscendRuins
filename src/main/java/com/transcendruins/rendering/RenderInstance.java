package com.transcendruins.rendering;

import java.util.ArrayList;
import java.util.HashMap;

import com.transcendruins.graphics3d.PolyGroup;
import com.transcendruins.graphics3d.Position3D;
import com.transcendruins.graphics3d.geometry.Triangle3D;
import com.transcendruins.world.assetinstances.models.ModelInstance;
import com.transcendruins.world.assetinstances.rendermaterials.RenderMaterialInstance;

/**
 * <code>RenderInstance</code>: A class representing a model to be rendered,
 * paired with its instance context.
 */
public final class RenderInstance {

    /**
     * <code>ArrayList&lt;PolyGroup&gt;</code>: The polygon groups to be rendered.
     */
    private final ArrayList<PolyGroup> polygonGroups;

    /**
     * Retrieves the polygon groups of this <code>RenderInstance</code> instance.
     * 
     * @return <code>ArrayList&lt;PolyGroup&gt;</code>: The
     *         <code>polygonGroups</code> field of this <code>RenderInstance</code>
     *         instance.
     */
    public ArrayList<PolyGroup> getPolygonGroups() {

        return polygonGroups;
    }

    /**
     * Creates a new instance of the <code>RenderInstance</code> class.
     * 
     * @param model          <code>ModelInstance</code>: The model to apply to this
     *                       <code>RenderInstance</code> instance.
     * @param renderMaterial <code>RenderMaterialInstance</code>: The render
     *                       material to render this <code>RenderInstance</code>
     *                       instance using.
     * @param boneActors     <code>HashMap&lt;String, Model.BoneActor&gt;</code>:
     *                       The bone actors used to animate the <code>model</code>
     *                       perameter using.
     * @param offset         <code>Position3D</code>: The offset at which to render
     *                       the <code>model</code> perameter from.
     */
    public RenderInstance(ModelInstance model, RenderMaterialInstance renderMaterial,
            HashMap<String, Model.BoneActor> boneActors, Position3D offset) {

        ArrayList<Triangle3D> polygonsAdjusted = model.getModel().getPolygons(boneActors, offset, model.getAngle(),
                model.getAxisHeading(), model.getAxisPitch());

        // Adjust every polygon in the retrieved polygons.
        for (Triangle3D polygonAdjusted : polygonsAdjusted) {

            polygonAdjusted.setRenderMaterial(renderMaterial);
        }

        // The group created from the set of models.
        PolyGroup modelGroup = new PolyGroup(polygonsAdjusted);

        // Recursively subdivides the polygon group until it is within the appropriate
        // size.
        polygonGroups = modelGroup.subDivide();
    }
}
