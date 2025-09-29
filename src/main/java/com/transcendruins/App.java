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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.elements.ElementContext;
import com.transcendruins.assets.elements.ElementInstance;
import com.transcendruins.assets.interfaces.InterfaceContext;
import com.transcendruins.assets.interfaces.InterfaceInstance;
import com.transcendruins.graphics3d.Camera3D;
import com.transcendruins.packs.PackProcessor;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.resources.styles.Style;
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

        PackProcessor packProcessor = PackProcessor.getProcessor();
        // packProcessor.addRoot();

        Identifier vanillaId = Identifier.createTestIdentifier("TranscendRuins:vanilla", new int[] { 1, 0, 0 });
        ContentPack vanillaPack = ContentPack.getPack(vanillaId);

        System.out.println(vanillaPack.getAssets().entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue().keySet().stream().toList()).toList());

        ArrayList<ContentPack> packs = new ArrayList<>();
        packs.add(vanillaPack);

        ArrayList<ResourcePack> resources = new ArrayList<>();

        long seed = 12397123057172l;
        System.out.println("SEED: " + seed);

        World.createWorld(packs, resources, seed);
        World world = World.getWorld();
        world.start();

        // System.out.println(cache);

        // long size = 0;
        // int count = 0;

        // System.out.println(TracedPath.formatSize(size));

        // for (TracedPath path : cache.listFiles()) {

        // count++;

        // long fileSize = path.getSize();
        // size += fileSize;

        // System.out.println();
        // System.out.println(path + " : " + TracedPath.formatSize(fileSize));
        // System.out.println(TracedPath.formatSize(size));
        // }
        // System.out.println(count + " files. Average size: " +
        // TracedPath.formatSize(size / count));

        // Identifier pyramidId =
        // Identifier.createTestIdentifier("TranscendRuins:pyramid", null);
        // ElementSchema pyramidSchema = (ElementSchema)
        // vanillaPack.getAsset(AssetType.ELEMENT, pyramidId);

        // ElementInstance pyramidInstance = new ElementInstance(pyramidSchema,
        // World.getWorld(), 0, 0, World.EAST,
        // Vector.IDENTITY_VECTOR);

        Identifier axesId = Identifier.createTestIdentifier("TranscendRuins:axes", null);

        Identifier boxId = Identifier.createTestIdentifier("TranscendRuins:box", null);

        AssetPresets examplePresets = new AssetPresets(boxId, AssetType.ELEMENT);
        ElementContext exampleContext = new ElementContext(examplePresets, world, null);

        ElementInstance example = (ElementInstance) AssetType.ELEMENT.createAsset(exampleContext);
        example.update(world.getRuntimeSeconds());

        // ElementInstance ex2 = new ElementInstance(examplePresets, world, 0, 0, //

        double startTime = world.getRuntimeSeconds();

        System.out.println("ELAPSED TIME: " + (world.getRuntimeSeconds() - startTime));

        ArrayList<RenderInstance> models;

        Camera3D camera = new Camera3D();

        // DisplayFrame frame = new DisplayFrame(camera);
        // Render3DPanel renderer = (Render3DPanel)
        // frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);

        models = new ArrayList<>();
        models.add(example);

        // synchronized (TIMER) {
        // while (true) {

        // renderer.render(models, camera);

        // TIMER.wait(20);
        // }
        // }

        Identifier interfaceId = Identifier.createTestIdentifier("TranscendRuins:playerInventory", null);
        AssetPresets interfacePresets = new AssetPresets(interfaceId, AssetType.INTERFACE);
        InterfaceContext interfaceContext = new InterfaceContext(interfacePresets, world, null, null);

        InterfaceInstance interfaceExample = (InterfaceInstance) AssetType.INTERFACE.createAsset(interfaceContext);
        interfaceExample.update(world.getRuntimeSeconds());

        BufferedImage image = interfaceExample.render(500, 500, 16, Style.EMPTY);
        System.out.println(image.getWidth() + " , " + image.getHeight());
        JFrame frame = new JFrame();
        frame.setBounds(50, 50, 800, 800);

        JLabel label = new JLabel(new ImageIcon(image));
        // frame.getContentPane().setBackground(Color.GRAY);
        frame.add(label);
        frame.setVisible(true);
        frame.repaint();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * Converts a snake-case string (ex_string) into camel-case (exString).
     * 
     * @param string <code>String</code>: The string to convert.
     * @return <code>String</code>: The resulting camel-case string.
     */
    public static final String toCamelCase(String string) {

        String[] tokens = string.toLowerCase().split("_");
        return tokens[0] + Arrays.stream(tokens, 1, tokens.length).map(token -> {

            return Character.toUpperCase(token.charAt(0)) + token.substring(1);
        }).collect(Collectors.joining());
    }

    public static final String toSentenceCase(String string) {

        String[] tokens = string.toLowerCase().split("_");
        return Arrays.stream(tokens).map(token -> {

            return Character.toUpperCase(token.charAt(0)) + token.substring(1);
        }).collect(Collectors.joining());
    }

    /**
     * Prevents the <code>App</code> class from being instantiated.
     */
    private App() {
    }
}
