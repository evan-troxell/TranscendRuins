package com.transcendruins.rendering.renderbuffer;

import java.nio.FloatBuffer;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
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

        boolean bc = materialData.backfaceCulling();

        int vLen = vertices.length;
        int vLenX2 = vLen * 2;

        int indLen = indices.length;
        int indLenX2 = indLen * 2;

        Vector3f[] verticesArray;
        Vector2f[] uvsArray;

        if (bc) {
            verticesArray = vertices;
            uvsArray = uvs;
        } else {
            verticesArray = new Vector3f[vLenX2];
            System.arraycopy(vertices, 0, verticesArray, 0, vLen);
            System.arraycopy(vertices, 0, verticesArray, vLen, vLen);

            uvsArray = new Vector2f[vLenX2];
            System.arraycopy(uvs, 0, uvsArray, 0, vLen);
            System.arraycopy(uvs, 0, uvsArray, vLen, vLen);
        }

        int[] indicesArray = new int[bc ? indLen : indLenX2];
        Vector3f[] normalsArray = new Vector3f[bc ? vLen : vLenX2];

        // Initialize normals
        for (int i = 0; i < normalsArray.length; i++) {
            normalsArray[i] = new Vector3f();
        }

        if (bc) {

            // ===== BACKFACE CULLING ENABLED =====
            for (int t = 0; t * 3 < indLen; t++) {

                int i0 = indices[t * 3];
                int i1 = indices[t * 3 + 1];
                int i2 = indices[t * 3 + 2];

                // Reverse winding (123 → 132)
                indicesArray[t * 3] = i0;
                indicesArray[t * 3 + 1] = i2;
                indicesArray[t * 3 + 2] = i1;

                Vector3f v0 = vertices[i0];
                Vector3f v1 = vertices[i1];
                Vector3f v2 = vertices[i2];

                Vector3f normal = v1.subtract(v0).cross(v2.subtract(v0)).normalizeLocal();

                normalsArray[i0].set(normal);
                normalsArray[i1].set(normal);
                normalsArray[i2].set(normal);
            }

        } else {

            // ===== BACKFACE CULLING DISABLED =====
            int vertexOffset = vLen;
            int indexOffset = indLen;

            for (int t = 0; t * 3 < indLen; t++) {

                int base = t * 3;

                int i0 = indices[base];
                int i1 = indices[base + 1];
                int i2 = indices[base + 2];

                // ---- Front face (reversed winding) ----
                indicesArray[base] = i0;
                indicesArray[base + 1] = i2;
                indicesArray[base + 2] = i1;

                // ---- Back face (normal winding, offset vertices) ----
                indicesArray[base + indexOffset] = i0 + vertexOffset;
                indicesArray[base + indexOffset + 1] = i1 + vertexOffset;
                indicesArray[base + indexOffset + 2] = i2 + vertexOffset;

                Vector3f v0 = vertices[i0];
                Vector3f v1 = vertices[i1];
                Vector3f v2 = vertices[i2];

                Vector3f normal = v1.subtract(v0).cross(v2.subtract(v0)).normalizeLocal();

                Vector3f negNormal = normal.negate();

                normalsArray[i0].set(normal);
                normalsArray[i1].set(normal);
                normalsArray[i2].set(normal);

                normalsArray[i0 + vertexOffset].set(negNormal);
                normalsArray[i1 + vertexOffset].set(negNormal);
                normalsArray[i2 + vertexOffset].set(negNormal);
            }
        }

        // ===== Upload to mesh =====
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verticesArray));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvsArray));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indicesArray));

        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normalsArray.length * 3);
        for (Vector3f n : normalsArray) {
            normalBuffer.put(n.x).put(n.y).put(n.z);
        }
        normalBuffer.flip();

        mesh.setBuffer(Type.Normal, 3, normalBuffer);

        mesh.updateCounts();
        mesh.updateBound();

        Geometry geom = new Geometry("Quad" + (quadCount++), mesh);
        geom.setMaterial(materialData.createMaterial(assetManager));

        return geom;
    }

    public final List<Light> getLights(Vector3f[] vertices) {

        return lights.stream().map(light -> light.createLight(vertices)).toList();
    }
}
