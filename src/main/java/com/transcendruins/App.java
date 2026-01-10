/* Copyright 2026 Evan Troxell
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.transcendruins.packs.PackProcessor;
import com.transcendruins.packs.content.ContentPack;
import com.transcendruins.packs.resources.ResourcePack;
import com.transcendruins.rendering.RenderInstance;
import com.transcendruins.utilities.files.DataConstants;
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

        System.out.println("START");

        // new SimpleApp().start();

        // new Test().start();

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

        World.createWorld(packs, resources, seed);
        World world = World.getWorld();
        world.setLanguage(World.LanguageType.ENGLISH);

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

        // ElementInstance ex2 = new ElementInstance(examplePresets, world, 0, 0, //

        ArrayList<RenderInstance> models;

        // DisplayFrame frame = new DisplayFrame(camera);
        // Render3DPanel renderer = (Render3DPanel)
        // frame.getScreen(DisplayFrame.RENDER_DISPLAY_SCREEN);
        // models = new ArrayList<>();
        // models.add(example);
        // renderer.render(models, camera);

        // Test app = new Test();
        // app.start();

        // synchronized (TIMER) {
        // while (true) {

        // renderer.render(models, camera);

        // if (true == false) {

        // break;
        // }
        // }
        // }

        long playerId = 0;
        boolean added = world.addPlayer(playerId);
        if (!added) {

            System.out.println("HOST COULD NOT START: PLAYER WAS NOT ADDED");
            return;
        }

        world.startHost();

        /* LAYOUT RENDERING */
        // ImmutableMap<Identifier, AssetSchema> layoutAssets =
        // vanillaPack.getAssets().get(AssetType.LAYOUT);
        // Identifier exampleLayout =
        // Identifier.createTestIdentifier("Example:exampleLayout", null);
        // AssetPresets layoutPresets = new AssetPresets(exampleLayout,
        // AssetType.LAYOUT);
        // LayoutContext layoutContext = new LayoutContext(layoutPresets,
        // world.getLocation("bunkerBravo"));
        // LayoutInstance layout = layoutContext.instantiate();

        // AreaGrid area = layout.generate();
        // int width = area.getWidth();
        // int length = area.getLength();

        // int scale = 15;

        // AreaTile[] tiles = area.getArea(0, 0, width, length);
        // BufferedImage areaImage = new BufferedImage(width * scale, length * scale,
        // BufferedImage.TYPE_INT_ARGB);
        // Graphics2D g2d = areaImage.createGraphics();

        // JFrame frame = new JFrame();

        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // double ratio = 0.4;
        // Rectangle bounds = new Rectangle((int) (screenSize.width * ratio), 0, (int)
        // (screenSize.width * (1 - ratio)),
        // screenSize.height);
        // frame.setBounds(bounds);

        // ArrayList<ElementInstance> elements = area.getElements();
        // int[] i = { 0 };

        // JPanel panel = new JPanel() {

        // @Override
        // public void paint(Graphics graphics) {

        // graphics.setColor(Color.GREEN.darker());
        // graphics.fillRect(0, 0, areaImage.getWidth(), areaImage.getHeight());

        // graphics.drawImage(areaImage, 0, 0, null);

        // if (i[0] < elements.size()) {

        // ElementInstance element = elements.get(i[0]);
        // Color mapColor = element.getMapColor();

        // if (mapColor == null) {

        // return;
        // }

        // Rectangle bounds = element.getTileBounds();

        // g2d.setColor(mapColor);
        // g2d.fillRect(bounds.x * scale, bounds.y * scale, bounds.width * scale,
        // bounds.height * scale);
        // }
        // i[0]++;
        // }
        // };

        // frame.add(panel);

        // panel.setSize(frame.getSize());

        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setIconImage(DataConstants.FRAME_ICON_PATH.retrieveImage().getImage());
        // frame.setVisible(true);

        // Object lock = new Object();
        // synchronized (lock) {

        // while (true) {

        // panel.repaint();
        // try {
        // lock.wait(25);
        // } catch (InterruptedException e) {
        // }
        // }
        // }

        /* UI RENDERING */
        JFrame uiFrame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double ratio = 0.4;
        Rectangle bounds = new Rectangle((int) (screenSize.width * ratio), 0, (int) (screenSize.width * (1 - ratio)),
                screenSize.height);
        uiFrame.setBounds(bounds);

        boolean[] hideMouse = { false };

        ImageIcon cursor = new ImageIcon("cursor.png");
        int cursorWidth = 15;
        int cursorHeight = (int) (cursor.getIconHeight() * (cursorWidth / (double) cursor.getIconWidth()));

        int[] frame = { 1 };
        int[] mousePos = { -1000, -1000 };
        boolean[] write = { false, false };

        JPanel panel = new JPanel() {

            @Override
            public void paint(Graphics graphics) {

                // World.StopWatch watch = new World.StopWatch();
                // watch.start();

                BufferedImage image = world.renderUi(playerId);
                Graphics2D g2d = image.createGraphics();
                if (!hideMouse[0]) {

                    g2d.drawImage(cursor.getImage(), mousePos[0] - 3, mousePos[1] - 3, cursorWidth, cursorHeight, null);
                }
                g2d.dispose();
                // watch.stop("Render time: ");

                float hue = (float) ((System.currentTimeMillis() * 0.00005) % 1.0f);
                Color rainbow = Color.getHSBColor(hue, 0.6f, 0.6f);
                graphics.setColor(rainbow);
                // graphics.setColor(new Color(128, 32, 32));
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

                graphics.drawImage(image, 0, 0, null);
                if (write[0]) {

                    new File("frames").mkdirs();

                    try {
                        ImageIO.write(image, "png",
                                new File("frames/frame_" + String.format("%04d", frame[0]) + ".png"));
                        frame[0]++;
                    } catch (IOException e) {

                        System.out.println(e);
                    }
                }

                if (write[1]) {

                    write[1] = false;

                    new File("screenshots").mkdirs();

                    try {
                        ImageIO.write(image, "png",
                                new File("screenshots/screenshot_" + Math.abs(new Random().nextLong()) + ".png"));
                    } catch (IOException e) {

                        System.out.println(e);
                    }
                }
            }
        };
        uiFrame.add(panel);

        world.setScreenSize(playerId, panel.getWidth(), panel.getHeight());

        panel.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {

                world.setScreenSize(playerId, panel.getWidth(), panel.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }

        });

        panel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

                world.playerConsumer(playerId, player -> player.setMousePress(true));
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                world.playerConsumer(playerId, player -> player.setMousePress(false));
            }

            @Override
            public void mouseEntered(MouseEvent e) {

                world.setMousePosition(playerId, e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {

                world.setMousePosition(playerId, e.getX(), e.getY());
            }

        });

        panel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                mousePos[0] = e.getX();
                mousePos[1] = e.getY();

                world.setMousePosition(playerId, e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                mousePos[0] = e.getX();
                mousePos[1] = e.getY();

                world.setMousePosition(playerId, e.getX(), e.getY());
            }

        });

        panel.addMouseWheelListener(e -> {

            int scroll = e.getUnitsToScroll();
            world.playerConsumer(playerId, player -> player.mouseScroll(0, scroll * 3));
        });

        uiFrame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyChar() == 'h') {

                    hideMouse[0] = !hideMouse[0];
                }

                if (e.getKeyChar() == 'e') {

                    write[0] = !write[0];
                }

                if (e.getKeyChar() == 'p') {

                    write[1] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });

        uiFrame.setIconImage(DataConstants.FRAME_ICON_PATH.retrieveImage().getImage());
        uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        uiFrame.setVisible(true);

        while (true) {

            panel.repaint();
        }
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
        }).collect(Collectors.joining(" "));
    }

    /**
     * Prevents the <code>App</code> class from being instantiated.
     */
    private App() {
    }
}
