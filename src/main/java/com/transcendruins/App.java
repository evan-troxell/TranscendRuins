/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins;

import java.util.ArrayList;

import com.transcendruins.contentmodules.ModuleProcessor;
import com.transcendruins.contentmodules.packs.Pack;
import com.transcendruins.contentmodules.resources.Resource;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.World;

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

        ModuleProcessor packProcessor = ModuleProcessor.getProcessor();
        // packProcessor.addRoot();

        Identifier vanillaId = Identifier.createTestIdentifier("TranscendRuins:vanilla", new int[] { 1, 0, 0 });
        Pack vanillaPack = Pack.getPack(vanillaId);

        System.out.println(vanillaPack.getAssets());

        ArrayList<Pack> packs = new ArrayList<>();
        packs.add(vanillaPack);

        ArrayList<Resource> resources = new ArrayList<>();

        World.createWorld(packs, resources);
        World world = World.getWorld();
        world.start();

        // Identifier pyramidId =
        // Identifier.createTestIdentifier("TranscendRuins:pyramid", null);
        // ElementSchema pyramidSchema = (ElementSchema)
        // vanillaPack.getAsset(AssetType.ELEMENT, pyramidId);

        // ElementInstance pyramidInstance = new ElementInstance(pyramidSchema,
        // World.getWorld(), 0, 0, World.EAST,
        // Vector.IDENTITY_VECTOR);

        Identifier axesId = Identifier.createTestIdentifier("TranscendRuins:axes",
                null);

        Identifier boxId = Identifier.createTestIdentifier("TranscendRuins:box", null);
        /*
         * ElementPresets examplePresets = new ElementPresets(new TracedEntry<>(null,
         * boxId));
         * ElementContext exampleContext = new ElementContext(examplePresets, world, 0,
         * 0, 0);
         * 
         * ElementInstance example = new ElementInstance(exampleContext);
         * example.update(world.getRuntimeSeconds());
         * 
         * // ElementInstance ex2 = new ElementInstance(examplePresets, world, 0, 0,
         * // World.NORTH, Vector.IDENTITY_VECTOR);
         * 
         * double startTime = world.getRuntimeSeconds();
         * 
         * for (int i = 0; i < Short.MAX_VALUE; i++) {
         * 
         * new ElementInstance(new EntityPresets(new TracedEntry<>(null, axesId)),
         * world, 0, 0, World.NORTH,
         * Vector.IDENTITY_VECTOR);
         * }
         * 
         * System.out.println("ELAPSED TIME: " + (world.getRuntimeSeconds() -
         * startTime));
         * 
         * ArrayList<RenderInstance> models;
         * 
         * Camera3D camera = new Camera3D();
         * 
         * DisplayFrame frame = new DisplayFrame(camera);
         * Render3D renderer = (Render3D)
         * frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);
         * 
         * models = new ArrayList<>();
         * models.add(example);
         * 
         * synchronized (TIMER) {
         * while (true) {
         * 
         * renderer.render(models, camera);
         * 
         * TIMER.wait(20);
         * }
         * }
         */
    }

    /**
     * Prevents the <code>App</code> class from being instantiated.
     */
    private App() {
    }
}
