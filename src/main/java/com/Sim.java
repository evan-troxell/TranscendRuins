package com;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.rendering.renderbuffer.MaterialData;
import com.transcendruins.rendering.renderbuffer.ModelData;
import com.transcendruins.world.World;

public final class Sim {

    public static final void main(String[] args) {

        int s = 6;
    }

    public static final Geometry createGeometry(int s, ModelAssetInstance asset, MaterialData materialData,
            AssetManager assetManager) {

        Vector3f[] vertices = new Vector3f[s * (s - 1)];
        Vector2f[] uvs = new Vector2f[s * (s - 1)];

        float pi_sm2 = FastMath.PI / (s - 2);
        float pi_s = FastMath.PI / s;

        float[] ringHeights = new float[s - 1];
        float[] ringZs = new float[s - 1];

        float totalHeight = 0;

        for (int x = 0; x < s - 1; x++) {

            float z = FastMath.sin(pi_sm2 * x - FastMath.HALF_PI);
            ringZs[x] = z;

            if (x > 0) {

                float dy = z - ringZs[x - 1];

                totalHeight += dy;
                ringHeights[x] = totalHeight;
            }

            float r = FastMath.sqrt(1.0f - z * z);

            for (int y = 0; y < s; y++) {

                float theta = 2 * pi_s * (y + 1) + pi_s * x;
                Vector3f v = new Vector3f(r * FastMath.cos(theta), r * FastMath.sin(theta), z);
                vertices[x * s + y] = v.multLocal(World.UNIT_TILE / 2)
                        .addLocal(new Vector3f(-World.UNIT_TILE, 0.5f * World.UNIT_TILE, -World.UNIT_TILE));

                Vector2f uv = new Vector2f(y / (float) (s - 1), ringHeights[x]);
                uvs[x * s + y] = uv;
            }
        }

        for (Vector2f uv : uvs) {

            uv.y /= totalHeight;
            uv.multLocal(32);
        }

        int[] indices = new int[6 * s * (s - 2)];

        for (int i = 0; i < indices.length / 6; i++) {

            indices[i * 6 + 2] = i;
            indices[i * 6 + 1] = s * (i / s) + (i + 1) % s;
            indices[i * 6] = i + s;
            indices[i * 6 + 3] = indices[i * 6];
            indices[i * 6 + 4] = indices[i * 6 + 2];
            indices[i * 6 + 5] = s + s * (i / s) + (i + s - 1) % s;
        }

        ModelData data = new ModelData(asset, uvs, indices, materialData, new ArrayList<>());
        Geometry mesh = data.getMesh(assetManager, vertices);

        return mesh;
    }
}
