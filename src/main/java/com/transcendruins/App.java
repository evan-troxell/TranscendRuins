package com.transcendruins;

import java.util.ArrayList;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.PackProcessor;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchema;
import com.transcendruins.packcompiling.assetschemas.entities.EntitySchema;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.ui.DisplayFrame;
import com.transcendruins.ui.Render3D;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.elements.ElementInstance;
import com.transcendruins.world.assetinstances.entities.EntityInstance;

/**
 * <code>App</code>: Hello world!
 */
public final class App {

    public static final Object TIMER = new Object();

    /**
     * Says hello to the world.
     * 
     * @param args <code>String[]</code> The arguments of the program.
     */
    public static void main(String[] args) throws Exception {

        PackProcessor packProcessor = PackProcessor.getProcessor();

        Identifier vanillaId = Identifier.createTestIdentifier("TranscendRuins:vanilla", new long[] { 1, 0, 0 });
        Pack vanillaPack = Pack.PACKS.get(vanillaId);

        ArrayList<Pack> packs = new ArrayList<>();
        packs.add(vanillaPack);
        World.buildWorld(packs);
        World world = World.getWorld();
        world.start();

        // Identifier pyramidId =
        // Identifier.createTestIdentifier("TranscendRuins:pyramid", null);
        // ElementSchema pyramidSchema = (ElementSchema)
        // vanillaPack.getAsset(AssetType.ELEMENT, pyramidId);

        // ElementInstance pyramidInstance = new ElementInstance(pyramidSchema,
        // World.getWorld(), 0, 0, World.EAST,
        // Vector.DEFAULT_VECTOR);

        Identifier axesId = Identifier.createTestIdentifier("TranscendRuins:axes",
                null);
        ElementSchema axesSchema = (ElementSchema) vanillaPack.getAsset(AssetType.ELEMENT, axesId);

        ElementInstance axesInstance = new ElementInstance(axesSchema,
                World.getWorld(), 0, 0, World.EAST,
                Vector.DEFAULT_VECTOR);

        Identifier exampleId = Identifier.createTestIdentifier("TranscendRuins:example", null);
        EntitySchema exampleSchema = (EntitySchema) vanillaPack.getAsset(AssetType.ENTITY, exampleId);

        EntityInstance exampleInstance = new EntityInstance(exampleSchema,
                World.getWorld(), 0, 0, World.NORTH,
                Vector.DEFAULT_VECTOR);

        ArrayList<RenderInstance> models;

        DisplayFrame frame = new DisplayFrame();
        Render3D renderer = (Render3D) frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);

        synchronized (TIMER) {
            while (true) {
                models = new ArrayList<>();

                // exampleInstance.onUpdate();
                // models.add(exampleInstance.getRenderInstance());

                axesInstance.onUpdate();
                models.add(axesInstance.getRenderInstance());

                renderer.render(models);

                TIMER.wait(20);
            }
        }
    }

    /**
     * Prevents the <code>App</code> class from being instantiated.
     */
    private App() {
    }
}
