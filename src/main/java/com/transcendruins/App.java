package com.transcendruins;

import java.util.ArrayList;

import com.transcendruins.graphics3d.geometry.Vector;
import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.PackProcessor;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.ui.DisplayFrame;
import com.transcendruins.ui.Render3D;
import com.transcendruins.utilities.json.TracedEntry;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.elements.ElementInstance;
import com.transcendruins.world.assetinstances.elements.ElementPresets;
import com.transcendruins.world.assetinstances.entities.EntityInstance;
import com.transcendruins.world.assetinstances.entities.EntityPresets;

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

        ElementPresets axesPresets = new ElementPresets(new TracedEntry<>(null, axesId));

        ElementInstance axesInstance = new ElementInstance(axesPresets,
                world, 0, 0, World.EAST,
                Vector.DEFAULT_VECTOR);

        Identifier exampleId = Identifier.createTestIdentifier("TranscendRuins:example", null);
        EntityPresets examplePresets = new EntityPresets(new TracedEntry<>(null, exampleId));

        EntityInstance ex1 = new EntityInstance(examplePresets,
                world, 0, 0, World.NORTH,
                Vector.DEFAULT_VECTOR);

        ex1.onUpdate();

        EntityInstance ex2 = new EntityInstance(examplePresets,
                world, 0, 0, World.NORTH,
                Vector.DEFAULT_VECTOR);

        double startTime = world.getRuntimeSeconds();

        for (int i = 0; i < 1_000_000; i++) {

            new ElementInstance(axesPresets,
                    world, 0, 0, World.NORTH,
                    Vector.DEFAULT_VECTOR);
        }

        System.out.println("ELAPSED TIME: " + (world.getRuntimeSeconds() - startTime));

        ArrayList<RenderInstance> models;

        DisplayFrame frame = new DisplayFrame();
        Render3D renderer = (Render3D) frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);

        synchronized (TIMER) {
            while (true) {
                models = new ArrayList<>();

                models.add(ex1.getRenderInstance());

                models.add(ex2.getRenderInstance());

                // axesInstance.onUpdate();
                // models.add(axesInstance.getRenderInstance());

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
