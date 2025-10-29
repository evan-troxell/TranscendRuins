package com.transcendruins.rendering.renderBuffer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;

public final record MaterialData(BufferedImage image, int textureWidth, int textureHeight, int materialBitMask,
        ColorRGBA diffuse, ColorRGBA ambient, int shininess, ColorRGBA specular, BufferedImage specularMap) {

    public final boolean isTransparent() {

        return getMaterialBit(5);
    }

    public final Material createMaterial(AssetManager assetManager) {

        boolean lit = getMaterialBit(0);
        Material material = new Material(assetManager,
                lit ? "Common/MatDefs/Light/Lighting.j3md" : "Common/MatDefs/Misc/Unshaded.j3md");

        if (lit) {

            // Update diffusion.
            boolean diffuses = getMaterialBit(1);
            if (diffuses) {

                material.setColor("Diffuse", diffuse);
                material.setBoolean("UseMaterialColors", true);
            }

            // Update ambience.
            boolean hasAmbient = getMaterialBit(2);
            if (hasAmbient) {

                material.setColor("Ambient", ambient);
            }

            // Update shininess.
            material.setFloat("Shininess", shininess);

            // Update specular or specular map.
            boolean hasSpecular = getMaterialBit(3);
            boolean hasSpecularMap = getMaterialBit(4);
            if (hasSpecularMap) {

                Texture2D specularMapTexture = toTexture(resizeImage(specularMap));
                material.setTexture("SpecularMap", specularMapTexture);
            } else if (hasSpecular) {

                material.setColor("Specular", specular);
            }

            // Update texture.
            material.setTexture("DiffuseMap", toTexture(image));
        } else {

            // Update texture.
            material.setTexture("ColorMap", toTexture(image));
        }

        // Update transparency.
        boolean transparent = getMaterialBit(5);
        if (transparent) {

            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }

        // Update backface culling
        boolean hasBackfaceCulling = getMaterialBit(6);
        if (hasBackfaceCulling) {

            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        }

        return material;
    }

    private boolean getMaterialBit(int i) {

        return ((materialBitMask >> i) & 1) == 1;
    }

    public BufferedImage resizeImage(BufferedImage original) {

        BufferedImage resized = new BufferedImage(textureWidth, textureHeight, original.getType());
        Graphics2D g = resized.createGraphics();

        g.drawImage(original, 0, 0, textureWidth, textureHeight, null);
        g.dispose();

        return resized;
    }

    public static final Texture2D toTexture(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int argb = image.getRGB(x, y);

                // Isolate ARGB values.
                byte a = (byte) ((argb >> 24) & 0xFF);
                byte r = (byte) ((argb >> 16) & 0xFF);
                byte g = (byte) ((argb >> 8) & 0xFF);
                byte b = (byte) (argb & 0xFF);

                buffer.put(r).put(g).put(b).put(a);
            }
        }

        // Reverse buffer order.
        buffer.flip();

        return new Texture2D(new Image(Image.Format.RGBA8, width, height, buffer, ColorSpace.Linear));
    }

}
