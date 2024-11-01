package com.transcendruins;

import java.util.ArrayList;

import com.transcendruins.geometry.Position3D;
import com.transcendruins.geometry.Vector;
import com.transcendruins.packcompiling.Pack;
import com.transcendruins.packcompiling.PackProcessor;
import com.transcendruins.packcompiling.assetschemas.AssetType;
import com.transcendruins.packcompiling.assetschemas.elements.ElementSchema;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.ui.DisplayFrame;
import com.transcendruins.ui.Render3D;
import com.transcendruins.utilities.files.FileOperator;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;
import com.transcendruins.world.assetinstances.elements.ElementInstance;

/**
 * <code>App</code>: Hello world!
 */
public final class App {

    /**
     * Says hello to the world.
     * @param args <code>String[]</code> The arguments of the program.
     */
    public static void main(String[] args) throws Exception {

        TracedPath internalPath = FileOperator.LOCAL_ROOT_DIRECTORY.extend("internal");

        PackProcessor packProcessor = PackProcessor.getProcessor();

        packProcessor.addRoot(internalPath.extend("vanilla_pack_versions"), true);

        packProcessor.validate();
        packProcessor.compile();

        Identifier vanillaId = Identifier.createTestIdentifier("transcendRuins:vanilla", new long[] {1, 0, 0});
        Pack vanillaPack = Pack.PACKS.get(vanillaId);

        ArrayList<Pack> packs = new ArrayList<>();
        packs.add(vanillaPack);
        World.buildWorld(packs);

        Identifier pyramidId = Identifier.createTestIdentifier("transcendRuins:pyramid", null);
        ElementSchema pyramidSchema = (ElementSchema) vanillaPack.getAsset(AssetType.ELEMENT, pyramidId);

        Identifier cubeId = Identifier.createTestIdentifier("transcendRuins:cube", null);
        ElementSchema cubeSchema = (ElementSchema) vanillaPack.getAsset(AssetType.ELEMENT, cubeId);

        ElementInstance pyramidInstance = new ElementInstance(pyramidSchema, 0, 0, World.EAST, new Position3D());
        ArrayList<ElementInstance> cubeInstances = new ArrayList<>();

        int width = 10;
        int length = 10;

        for (int x = 0; x < width; x++) {

            for (int z = 0; z < length; z++) {

                Position3D cubePosition = new Position3D(Vector.DEFAULT_VECTOR, 45, 0, false);

                cubeInstances.add(new ElementInstance(cubeSchema, x, z, 0, cubePosition));
            }
        }

        DisplayFrame frame = new DisplayFrame();

        ArrayList<RenderInstance> models = new ArrayList<>();
        models.add(pyramidInstance.getRenderInstance());

        for (ElementInstance cubeInstance : cubeInstances) {

            cubeInstance.getPosition().rotateBy(0, 90, false);
            models.add(cubeInstance.getRenderInstance());
        }

        Render3D renderer = (Render3D) frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);
        renderer.render(models);
    }

    /**
     * Prevents the <code>App</code> class from being instantiated.
     */
    private App() {}
}
