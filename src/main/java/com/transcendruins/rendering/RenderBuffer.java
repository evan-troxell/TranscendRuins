package com.transcendruins.rendering;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.rendermaterials.RenderMaterialInstance;
import com.transcendruins.graphics3d.geometry.Vector;

public final class RenderBuffer {

    private final ArrayList<Vector> vertices;

    private final ArrayList<MeshData> meshes;

    public RenderBuffer() {

        vertices = new ArrayList<>();
        meshes = new ArrayList<>();
    }

    public RenderBuffer(List<Vector> vertices, List<Vector> uvs, List<Integer> indices, BufferedImage texture,
            RenderMaterialInstance renderMaterial) {

        this.vertices = new ArrayList<>(vertices);

        meshes = new ArrayList<>(1);
        meshes.add(new MeshData(uvs, indices, texture, renderMaterial));
    }

    public final void append(RenderBuffer buffer) {

        vertices.addAll(buffer.vertices);
        meshes.addAll(buffer.meshes);
    }

    public final void append(List<RenderBuffer> buffers) {

        buffers.stream().forEach(buffer -> {

            vertices.addAll(buffer.vertices);
            meshes.addAll(buffer.meshes);
        });
    }

    public final void transform(BoneActor boneActor, Vector pivotPoint) {

        List<Vector> verticesTransformed = vertices.stream().map(vertex -> boneActor.transform(vertex, pivotPoint))
                .toList();

        vertices.clear();
        vertices.addAll(verticesTransformed);
    }
}
