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

package com.transcendruins.utilities.files;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

/**
 * <code>InternalPath</code>: A class representing a path to an external file.
 */
public final class InternalPath extends TracedPath {

    private static final int NONE = 0;

    private static final int FILE = 1;

    private static final int DIRECTORY = 2;

    public static final InternalPath ROOT;

    static {

        try (ScanResult scanResult = new ClassGraph().acceptPaths("assets").scan()) {

            ROOT = new InternalPath(DIRECTORY, 0);

            ResourceList resources = scanResult.getAllResources();

            for (Resource resource : resources) {

                long bytes = resource.getLength();

                String path = resource.getPathRelativeToClasspathElement();
                ROOT.addChild(0, bytes, path.split("/"));
            }

        } catch (Exception e) {

            throw new RuntimeException("Unable to scan resources", e);
        }
    }

    /**
     * <code>int</code>: Whether this <code>ExternalPath</code> instance is a file,
     * directory, or neither.
     */
    private final int type;

    /**
     * <code>long</code>: The file size of this <code>ExternalPath</code> instance
     * in bytes.
     */
    private long size;

    @Override
    public long getSize() {

        return size;
    }

    /**
     * <code>HashMap&lt;String, InternalPath&gt;</code>: The child paths of this
     * <code>InternalPath</code> instance, or <code>null</code> if it is not a
     * directory.
     */
    private final HashMap<String, InternalPath> children;

    /**
     * Adds a child path to this <code> InternalPath</code> instance.
     * 
     * @param depth      <code>int</code>: The current file depth, ranging from
     *                   <code>0</code> to <code>components.length - 1</code>.
     * @param bytes      <code>long</code>: The size, in bytes, of the file being
     *                   added.
     * @param components <code>String...</code>: The array of file components to
     *                   trace the added path to.
     * @return <code>InternalPath</code>: The resulting <code>TracedPath</code>
     *         instance.
     */
    private InternalPath addChild(int depth, long bytes, String... components) {

        size += (bytes > 0) ? bytes : 0;

        String[] added = Arrays.copyOfRange(components, 0, depth + 1);
        String next = added[depth];

        boolean isLast = added.length == components.length;

        InternalPath child = children.computeIfAbsent(next,
                _ -> new InternalPath(isLast ? FILE : DIRECTORY, isLast ? bytes : 0, added));

        return isLast ? child : child.addChild(depth + 1, bytes, components);
    }

    public static InternalPath createPath(String... components) {

        InternalPath path = ROOT;

        for (String component : components) {

            InternalPath next = path.isDirectory() ? path.children.get(component) : null;
            if (next == null) {

                return new InternalPath(NONE, -1, components);
            }

            path = next;
        }

        return path;
    }

    /**
     * Creates a new instance of the <code>ExternalPath</code> class.
     * 
     * @param type       <code>int</code>: Whether this <code>ExternalPath</code>
     *                   instance is a file, directory, or neither.
     * @param size       <code>long</code>: The file size of this
     *                   <code>ExternalPath</code> instance in bytes.
     * @param components <code>String...</code>: The path to the file.
     */
    private InternalPath(int type, long size, String... components) {

        super(true, components);

        this.size = size;

        children = type == DIRECTORY ? new HashMap<>() : null;
        this.type = type;
    }

    @Override
    public InternalPath extend(String... components) {

        Stream<String> stream = getComponents().stream();
        String[] path = Stream.concat(stream, Arrays.stream(components)).toArray(String[]::new);

        return createPath(path);
    }

    @Override
    public boolean exists() {

        return type != NONE;
    }

    @Override
    public boolean isFile() {

        return type == FILE;
    }

    @Override
    public boolean isDirectory() {

        return type == DIRECTORY;
    }

    @Override
    public InputStream getInputStream() {

        return getResourceAsStream(toString());
    }

    @Override
    public ArrayList<InternalPath> compileEntries() {

        return children == null ? new ArrayList<>() : new ArrayList<>(children.values());
    }
}
