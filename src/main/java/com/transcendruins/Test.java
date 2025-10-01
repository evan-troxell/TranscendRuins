package com.transcendruins;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;

public class Test extends SimpleApplication {

    private Geometry uiQuad;
    private final int uiWidth = 400, uiHeight = 200;

    // Use AtomicReference for thread-safe image swapping
    private final AtomicReference<BufferedImage> uiImageRef = new AtomicReference<>();

    @Override
    public void simpleInitApp() {

        BufferedImage initialUI = generateUI("Initial UI");
        uiImageRef.set(initialUI);

        // 2️⃣ Create a quad to display the UI
        Quad quad = new Quad(uiWidth, uiHeight);
        uiQuad = new Geometry("UIOverlay", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        Texture2D tex = toTexture(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        uiQuad.setMaterial(mat);
        uiQuad.setQueueBucket(RenderQueue.Bucket.Gui); // overlay

        uiQuad.setLocalTranslation(50, 50, 0);
        guiNode.attachChild(uiQuad);

        setupInputMappings();

        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);

        new Thread(this::hostSimulationLoop).start();
    }

    // Host simulation loop updates the UI BufferedImage
    private void hostSimulationLoop() {
        int frame = 0;
        while (true) {
            // Generate new UI image (simulated dynamic content)
            BufferedImage newUI = generateUI("Frame " + frame);
            uiImageRef.set(newUI);

            // Push update to jME render thread
            enqueue(() -> {
                // Texture2D tex = new Texture2D(new AWTLoader().load(uiImageRef.get(), true));
                // uiQuad.getMaterial().setTexture("ColorMap", tex);
            });

            frame++;
            try {
                Thread.sleep(100); // simulate fixed timestep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Simple dynamic UI generator
    private BufferedImage generateUI(String text) {
        BufferedImage img = new BufferedImage(uiWidth, uiHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, uiWidth, uiHeight);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(text, 20, 100);
        g2d.dispose();
        return img;
    }

    // Input mappings for mouse, keyboard, vertical & horizontal scroll
    private void setupInputMappings() {
        inputManager.addMapping("LeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        inputManager.addMapping("MotionHorizontal", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MotionVertical", new MouseAxisTrigger(MouseInput.AXIS_Y, false));

        inputManager.addMapping("KeyW", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("KeyS", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addListener(actionListener, "LeftClick", "RightClick", "KeyW", "KeyS");
        inputManager.addListener(analogListener, "MotionHorizontal", "MotionVertical");

        // GLFW.glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
        // System.out.println("Scroll X=" + xoffset + " Y=" + yoffset);
        // });
    }

    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (isPressed) {
            switch (name) {
            case "LeftClick" -> System.out.println("Mouse left clicked!");
            case "RightClick" -> System.out.println("Mouse right clicked!");
            case "KeyW" -> System.out.println("W pressed!");
            case "KeyS" -> System.out.println("S pressed!");
            }
        }
    };

    private final AnalogListener analogListener = (name, value, tpf) -> {
        switch (name) {
        case "ScrollUp" -> System.out.println("Scrolled up: " + value);
        case "ScrollDown" -> System.out.println("Scrolled down: " + value);
        case "ScrollLeft" -> System.out.println("Scrolled left: " + value);
        case "ScrollRight" -> System.out.println("Scrolled right: " + value);
        case "MotionHorizontal" -> System.out.println("Mouse moved horizontally: " + value);
        case "MotionVertical" -> System.out.println("Mouse moved vertically: " + value);
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        // Optional: per-frame updates can go here
        // For example, check mouse hover over UI quad
    }

    public static final Texture2D toTexture(BufferedImage awtImage) {
        int width = awtImage.getWidth();
        int height = awtImage.getHeight();

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = awtImage.getRGB(x, y);

                byte a = (byte) ((argb >> 24) & 0xFF);
                byte r = (byte) ((argb >> 16) & 0xFF);
                byte g = (byte) ((argb >> 8) & 0xFF);
                byte b = (byte) (argb & 0xFF);

                buffer.put(r).put(g).put(b).put(a);
            }
        }
        buffer.flip();

        Image image = new Image(Image.Format.RGBA8, width, height, buffer, ColorSpace.Linear);
        return new Texture2D(image);
    }
}