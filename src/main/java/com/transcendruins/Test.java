package com.transcendruins;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.packs.PackProcessor;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.rendering.RenderPacket;
import com.transcendruins.utilities.files.DataConstants;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;

public class Test extends SimpleApplication implements ActionListener, AnalogListener {

    private static World world;
    private static final long playerId = 0;

    private boolean mouseDown = false;

    private final float mouseSensitivity = 1.5f;
    private float pitch = 0.1f * FastMath.PI;
    private float yaw = 0.1725f * FastMath.PI;

    private final Vector3f walkDirection = new Vector3f();
    private final float moveSpeed = 1.25f;

    private final HashMap<ModelAssetInstance, Geometry> geoms = new HashMap<>();

    private final HashMap<ModelAssetInstance, Light> lights = new HashMap<>();

    public static void main(String[] args) {

        PackProcessor packProcessor = PackProcessor.getProcessor();
        // packProcessor.addRoot();

        ContentPack vanillaPack = ContentPack.getPack(DataConstants.VANILLA_IDENTIFIER);

        ArrayList<ContentPack> packs = new ArrayList<>();
        packs.add(vanillaPack);

        Identifier examplePackId = Identifier.createTestIdentifier("Example:examplePack", new int[] { 1, 0, 0 });
        ContentPack examplePack = ContentPack.getPack(examplePackId);

        System.out.println(examplePack.getAssets().entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue().keySet().stream().toList()).toList());
        packs.add(examplePack);

        ArrayList<ResourcePack> resources = new ArrayList<>();

        long seed = (long) (Math.random() * 1000000000l);
        System.out.println("SEED: " + seed); // 645221640

        world = World.createWorld(packs, resources, seed);
        world.setLanguage(World.LanguageType.ENGLISH);

        Test app = new Test();

        boolean added = world.addPlayer(playerId);
        if (!added) {

            System.out.println("HOST COULD NOT START: PLAYER WAS NOT ADDED");
            return;
        }

        world.startHost();
        world.enterLocation(playerId);

        AppSettings settings = new AppSettings(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double ratio = 0.4;
        Rectangle bounds = new Rectangle((int) (screenSize.width * ratio), 0, (int) (screenSize.width * (1 - ratio)),
                screenSize.height);

        settings.setFullscreen(false);
        settings.setCenterWindow(false);

        settings.setWindowXPosition(bounds.x);
        settings.setWindowYPosition(bounds.y);
        settings.setWindowSize(bounds.width, bounds.height);

        settings.setTitle("Transcend Ruins");

        app.setSettings(settings);
        app.setShowSettings(false);

        app.start();
    }

    private Geometry uiQuad;
    private final int uiWidth = 400, uiHeight = 200;

    // Use AtomicReference for thread-safe image swapping
    private final AtomicReference<BufferedImage> uiImageRef = new AtomicReference<>(generateUI("Initial UI"));

    @Override
    public void simpleInitApp() {

        setDisplayFps(false);
        setDisplayStatView(false);

        flyCam.setEnabled(false);

        Quad quad = new Quad(uiWidth, uiHeight);
        uiQuad = new Geometry("UIOverlay", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        Texture2D tex = toTexture(uiImageRef.get());
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        uiQuad.setMaterial(mat);
        uiQuad.setQueueBucket(RenderQueue.Bucket.Gui); // overlay

        // uiQuad.setLocalTranslation(50, 50, 0);
        // guiNode.attachChild(uiQuad);

        setUpKeys();

        // setupInputMappings();

        // inputManager.setCursorVisible(true);
        // flyCam.setEnabled(false);

        // new Thread(this::hostSimulationLoop).start();

        // Box b = new Box(1f, 1f, 1f);
        // Material geomMat = new Material(assetManager,
        // "Common/MatDefs/Light/Lighting.j3md");
        // Texture geomTex = assetManager.loadTexture("Textures/box.png");
        // geomMat.setTexture("DiffuseMap", geomTex);

        // geomMat.setBoolean("UseMaterialColors", true);
        // geomMat.setColor("Diffuse", ColorRGBA.White);
        // geomMat.setColor("Specular", ColorRGBA.White);
        // geomMat.setFloat("Shininess", 16f);

        // Geometry geom = new Geometry("Box", b);
        // geom.setMaterial(geomMat);
        // rootNode.attachChild(geom);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, 1, 0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.125f)); // adjust intensity
        rootNode.addLight(ambient);

        cam.setLocation(new Vector3f(30 * 1.8f, 40, 42 * 1.8f));
        // cam.getRotation().multLocal(new Quaternion().fromAngleAxis(3.1415926f / 2,
        // new Vector3f(0, 1, 0)));

        updateScene();
    }

    private void updateScene() {

        RenderPacket packet = world.getPolygons(playerId).getRenderPacket(assetManager);

        for (Map.Entry<ModelAssetInstance, Geometry> entry : packet.opaque().entrySet()) {

            ModelAssetInstance asset = entry.getKey();
            Geometry geom = entry.getValue();

            if (geoms.containsKey(asset)) {

                geoms.get(asset).setMesh(geom.getMesh());
            } else {

                rootNode.attachChild(geom);
                geoms.put(asset, geom);
            }
        }

        for (Map.Entry<ModelAssetInstance, Geometry> entry : packet.transparent().entrySet()) {

            ModelAssetInstance asset = entry.getKey();
            Geometry geom = entry.getValue();

            if (geoms.containsKey(asset)) {

                geoms.get(asset).setMesh(geom.getMesh());
            } else {

                rootNode.attachChild(geom);
                geoms.put(asset, geom);
            }
        }

        for (Map.Entry<ModelAssetInstance, Light> entry : packet.lights().entrySet()) {

            ModelAssetInstance asset = entry.getKey();
            Light light = entry.getValue();

            if (lights.containsKey(asset)) {

                Light base = lights.get(asset);

                // base.setPosition(light.ge);
                // base.setColor(newColor);
                // base.setRadius(newRadius);
                // base.set

                // lights.get(asset).setMesh(light.getMesh());
            } else {

                rootNode.addLight(light);
                lights.put(asset, light);
            }
        }
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
        // Graphics2D g2d = (Graphics2D) img.getGraphics();
        // g2d.rotate(Math.toRadians(180), uiWidth / 2, uiHeight / 2);
        // g2d.translate(uiWidth / 2, uiHeight / 2);
        // g2d.scale(-1, 1);
        // g2d.translate(-uiWidth / 2, -uiHeight / 2);
        // g2d.setColor(Color.RED);
        // g2d.fillRect(0, 0, uiWidth, uiHeight);
        // g2d.setColor(Color.WHITE);
        // g2d.setFont(new Font("Arial", Font.BOLD, 20));
        // g2d.drawString(text, 20, 100);
        // g2d.dispose();
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

        inputManager.addListener(this, "LeftClick", "RightClick", "KeyW", "KeyS");
        inputManager.addListener(this, "MotionHorizontal", "MotionVertical");

        // GLFW.glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
        // System.out.println("Scroll X=" + xoffset + " Y=" + yoffset);
        // });
    }

    private void setUpKeys() {

        inputManager.addMapping("LeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("CamRightKey", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("CamLeftKey", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("CamUpKey", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("CamDownKey", new KeyTrigger(KeyInput.KEY_DOWN));

        inputManager.addMapping("CamRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("CamLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("CamUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("CamDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addListener(this, "LeftClick", "Forward", "Backward", "Left", "Right", "CamRight", "CamRightKey",
                "CamLeft", "CamLeftKey", "CamUp", "CamUpKey", "CamDown", "CamDownKey");

        inputManager.setCursorVisible(false);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // if (isPressed) {
        // switch (name) {
        // case "LeftClick" -> System.out.println("Mouse left clicked!");
        // case "RightClick" -> System.out.println("Mouse right clicked!");
        // case "KeyW" -> System.out.println("W pressed!");
        // case "KeyS" -> System.out.println("S pressed!");
        // }
        // }

        if (name.equals("LeftClick")) {
            mouseDown = isPressed;
        }
    };

    @Override
    public void onAnalog(String name, float value, float tpf) {

        boolean positionMoved = false;
        Vector3f direction = new Vector3f(0, 0, 0);

        boolean camMoved = false;
        Quaternion camRot = new Quaternion(0, 0, 0, 1);

        Vector3f straight = cam.getDirection().normalize();
        Vector3f left = cam.getLeft().normalize();
        Vector3f up = cam.getUp().normalize();

        switch (name) {
        case "Forward" -> {

            positionMoved = true;
            direction.addLocal(straight);
        }
        case "Backward" -> {

            positionMoved = true;
            direction.addLocal(straight.mult(-1));
        }
        case "Left" -> {

            positionMoved = true;
            direction.addLocal(left);
        }
        case "Right" -> {

            positionMoved = true;
            direction.addLocal(left.mult(-1));
        }

        case "CamRight" -> {
            if (mouseDown) {

                camMoved = true;
                yaw -= mouseSensitivity * value;
            }
        }
        case "CamLeft" -> {
            if (mouseDown) {

                camMoved = true;
                yaw += mouseSensitivity * value;
            }
        }
        case "CamUp" -> {
            if (mouseDown) {

                camMoved = true;
                pitch -= mouseSensitivity * value * 0.6;
            }
        }
        case "CamDown" -> {
            if (mouseDown) {

                camMoved = true;
                pitch += mouseSensitivity * value * 0.6;
            }
        }
        case "CamRightKey" -> {

            camMoved = true;
            yaw -= mouseSensitivity * 0.00625;
        }
        case "CamLeftKey" -> {

            camMoved = true;
            yaw += mouseSensitivity * 0.00625;
        }
        case "CamUpKey" -> {

            camMoved = true;
            pitch -= mouseSensitivity * 0.00625;
        }
        case "CamDownKey" -> {

            camMoved = true;
            pitch += mouseSensitivity * 0.00625;
        }
        }

        if (positionMoved) {

            direction.y = 0;

            direction.normalizeLocal();
            walkDirection.addLocal(direction);
        }

        if (camMoved) {

            // pitch = FastMath.clamp(pitch, -0.4f * FastMath.HALF_PI + 0.001f, 0.4f *
            // FastMath.HALF_PI - 0.001f);

            Quaternion qYaw = new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y);
            Quaternion qPitch = new Quaternion().fromAngleAxis(pitch, Vector3f.UNIT_X);

            cam.setRotation(new Quaternion(0, 1, 0, 0).mult(qYaw).mult(qPitch));
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        // Check mouse hover over UI quad
        // enqueue()

        updateScene();

        walkDirection.normalizeLocal().multLocal(moveSpeed);
        cam.setLocation(cam.getLocation().add(walkDirection));
        walkDirection.set(0, 0, 0);

        float change = 0.0015f;

        yaw += change;
        Quaternion qYaw = new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y);
        Quaternion qPitch = new Quaternion().fromAngleAxis(pitch, Vector3f.UNIT_X);
        cam.setRotation(new Quaternion(0, 1, 0, 0).mult(qYaw).mult(qPitch));
        cam.setLocation(new Vector3f(12 + 90 * (float) Math.sin(yaw), 40, 12 + 90 * (float) Math.cos(yaw)));
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