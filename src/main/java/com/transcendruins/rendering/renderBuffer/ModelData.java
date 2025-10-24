package com.transcendruins.rendering.renderBuffer;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public final record ModelData(Vector2f[] uvs, int[] indices, MaterialData materialData, List<LightData> lights) {

    public final int getVertexCount() {

        return uvs().length;
    }

    public final Geometry getMesh(AssetManager assetManager, Vector3f[] vertices) {

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvs()));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices()));
        mesh.updateBound();

        Geometry quad = new Geometry("CustomQuad", mesh);

        Material material = materialData.createMaterial(assetManager);
        quad.setMaterial(material);

        return quad;
    }
}
