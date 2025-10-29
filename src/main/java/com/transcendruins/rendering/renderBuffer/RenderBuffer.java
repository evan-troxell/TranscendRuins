package com.transcendruins.rendering.renderBuffer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.geometry.Vector;
import com.transcendruins.rendering.RenderPacket;

public final class RenderBuffer {

    private final ArrayList<Vector> vertices;

    private final ArrayList<ModelData> models;

    public RenderBuffer() {

        vertices = new ArrayList<>();
        models = new ArrayList<>();
    }

    public RenderBuffer(List<Vector> vertices, List<Vector2f> uvs, List<Integer> indices, BufferedImage texture,
            int textureWidth, int textureHeight, RenderMaterialInstance renderMaterial, List<LightData> lights) {

        this.vertices = new ArrayList<>(vertices);

        models = new ArrayList<>(1);

        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indexArray.length; i++) {

            indexArray[i] = indices.get(i);
        }

        models.add(new ModelData(uvs.toArray(i -> new Vector2f[i]), indexArray,
                renderMaterial.createMaterialData(texture, textureWidth, textureHeight), lights));
    }

    public final synchronized void append(RenderBuffer buffer) {

        vertices.addAll(buffer.vertices);
        models.addAll(buffer.models);
    }

    public final synchronized void append(List<RenderBuffer> buffers) {

        buffers.stream().forEach(buffer -> {

            vertices.addAll(buffer.vertices);
            models.addAll(buffer.models);
        });
    }

    public final synchronized void transform(BoneActor boneActor, Vector pivotPoint) {

        List<Vector> verticesTransformed = vertices.stream().map(vertex -> boneActor.transform(vertex, pivotPoint))
                .toList();

        vertices.clear();
        vertices.addAll(verticesTransformed);
    }

    public final synchronized RenderPacket getRenderPacket(AssetManager assetManager) {

        int offset = 0;
        Vector3f[] vertexArray = vertices.stream()
                .map(v -> new Vector3f((float) v.getX(), (float) v.getY(), (float) v.getZ()))
                .toArray(i -> new Vector3f[i]);

        ArrayList<Geometry> opaqueMeshes = new ArrayList<>();
        ArrayList<Geometry> transparentMeshes = new ArrayList<>();
        ArrayList<Light> lights = new ArrayList<>();

        for (ModelData model : models) {

            int vertexCount = model.getVertexCount();
            Vector3f[] meshVertices = new Vector3f[vertexCount];
            System.arraycopy(vertexArray, offset, meshVertices, 0, vertexCount);

            boolean isTransparent = model.materialData().isTransparent();
            Geometry geometry = model.getMesh(assetManager, meshVertices);

            if (isTransparent) {

                transparentMeshes.add(geometry);
            } else {

                opaqueMeshes.add(geometry);
            }

            lights.addAll(model.getLights(meshVertices));

            offset += vertexCount;
        }

        return new RenderPacket(opaqueMeshes, transparentMeshes, lights);
    }
}
