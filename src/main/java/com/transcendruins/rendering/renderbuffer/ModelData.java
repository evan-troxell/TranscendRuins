package com.transcendruins.rendering.renderbuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import com.transcendruins.assets.modelassets.ModelAssetInstance;

public final record ModelData(ModelAssetInstance asset, Vector2f[] uvs, int[] indices, MaterialData materialData,
        List<LightData> lights) {

    private static long quadCount = 0;

    public final int getVertexCount() {

        return uvs().length;
    }

    public final Geometry getMesh(AssetManager assetManager, Vector3f[] vertices) {

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvs()));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices()));
        mesh.updateBound();

        mesh.setMode(Mesh.Mode.Triangles);

        mesh.updateCounts();
        mesh.updateBound();

        // Remove the averaging loop and do flat shading per triangle
        FloatBuffer normals = BufferUtils.createFloatBuffer(vertices.length * 3);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices());
        indexBuffer.rewind();

        // Pre-fill with zero vectors
        Vector3f[] normalArray = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            normalArray[i] = new Vector3f();
        }

        // Compute flat normals per triangle
        while (indexBuffer.hasRemaining()) {
            int i0 = indexBuffer.get();
            int i1 = indexBuffer.get();
            int i2 = indexBuffer.get();

            Vector3f v0 = vertices[i0];
            Vector3f v1 = vertices[i1];
            Vector3f v2 = vertices[i2];

            // Triangle normal
            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);
            Vector3f triNormal = edge1.cross(edge2).normalizeLocal();

            // Assign the same normal to each vertex of the triangle (flat shading)
            normalArray[i0].set(triNormal);
            normalArray[i1].set(triNormal);
            normalArray[i2].set(triNormal);
        }

        // Write to buffer
        for (Vector3f n : normalArray) {
            normals.put(n.x).put(n.y).put(n.z);
        }
        normals.flip();

        mesh.setBuffer(VertexBuffer.Type.Normal, 3, normals);

        Geometry quad = new Geometry("Quad" + (quadCount++), mesh);

        Material material = materialData.createMaterial(assetManager);
        quad.setMaterial(material);

        return quad;
    }

    public final List<Light> getLights(Vector3f[] vertices) {

        return lights.stream().map(light -> light.createLight(vertices)).toList();
    }
}
