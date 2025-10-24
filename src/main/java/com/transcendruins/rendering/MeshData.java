package com.transcendruins.rendering;

import java.awt.image.BufferedImage;
import java.util.List;

import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.graphics3d.geometry.Vector;

public final class MeshData {

    private final List<Vector> uvs;

    private final List<Integer> indices;

    private final BufferedImage texture;

    private final RenderMaterialInstance renderMaterial;

    public final int getVertexCount() {

        return uvs.size();
    }

    public MeshData(List<Vector> uvs, List<Integer> indices, BufferedImage texture,
            RenderMaterialInstance renderMaterial) {

        this.uvs = uvs;
        this.indices = indices;
        this.texture = texture;
        this.renderMaterial = renderMaterial;
    }
}
