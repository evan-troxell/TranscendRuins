package com.transcendruins.rendering.renderbuffer;

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

    public final boolean backfaceCulling() {

        return getMaterialBit(6);
    }

    public Material createMaterial(AssetManager assetManager) {

        boolean lit = getMaterialBit(0);

        Material material = new Material(assetManager,
                lit ? "Common/MatDefs/Light/Lighting.j3md" : "Common/MatDefs/Misc/Unshaded.j3md");

        if (lit) {

            boolean useMaterialColors = false;

            // Diffuse color
            if (getMaterialBit(1)) {
                material.setColor("Diffuse", diffuse);
                useMaterialColors = true;
            }

            // Ambient color
            if (getMaterialBit(2)) {
                material.setColor("Ambient", ambient);
                useMaterialColors = true;
            }

            if (useMaterialColors) {
                material.setBoolean("UseMaterialColors", true);
            }

            // Shininess (safe default if unset)
            material.setFloat("Shininess", Math.max(1f, shininess));

            // Specular
            if (getMaterialBit(4) && specularMap != null) {
                material.setTexture("SpecularMap", toTexture(resizeImage(specularMap), ColorSpace.Linear));
            } else if (getMaterialBit(3)) {
                material.setColor("Specular", specular);
            }

            // Diffuse texture (sRGB!)
            if (image != null) {
                material.setTexture("DiffuseMap", toTexture(image, ColorSpace.sRGB));
            }

        } else {
            // Unlit material
            if (image != null) {
                material.setTexture("ColorMap", toTexture(image, ColorSpace.sRGB));
            }
        }

        // Transparency
        if (getMaterialBit(5)) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }

        // Disable backface culling. To enable backface culling, duplicate each face
        // with reversed normals to properly account for lighting artifact.
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);

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

    public static Texture2D toTexture(BufferedImage image, ColorSpace colorSpace) {

        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int argb = image.getRGB(x, y);

                buffer.put((byte) ((argb >> 16) & 0xFF)); // R
                buffer.put((byte) ((argb >> 8) & 0xFF)); // G
                buffer.put((byte) (argb & 0xFF)); // B
                buffer.put((byte) ((argb >> 24) & 0xFF)); // A
            }
        }

        buffer.flip();

        Image img = new Image(Image.Format.RGBA8, width, height, buffer, colorSpace);

        return new Texture2D(img);
    }
}
