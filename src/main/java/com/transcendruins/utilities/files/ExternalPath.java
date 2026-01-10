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

package com.transcendruins.utilities.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * <code>ExternalPath</code>: A class representing a path to an external file.
 */
public final class ExternalPath extends TracedPath {

    /**
     * <code>Path</code>: An absolute path encompassing the file pathway to this
     * <code>ExternalPath</code> instance.
     */
    private final Path path;

    /**
     * Creates a new instance of the <code>ExternalPath</code> class.
     * 
     * @param components <code>String...</code>: The path to the file.
     */
    protected ExternalPath(String... components) {

        super(false, components);

        path = Path.of("/", toString().split("/"));
    }

    /**
     * Creates a new file from the path of this <code>ExternalPath</code> instance.
     * 
     * @return <code>boolean</code>: Whether or not the file already existed.
     * @throws IOException Thrown if the designated file cannot be created for any
     *                     reason.
     */
    public boolean createFile() throws IOException {

        if (isFile()) {

            return true;
        }

        Files.createFile(path);
        return false;
    }

    /**
     * Creates a new directory from the path of this <code>ExternalPath</code>
     * instance.
     * 
     * @return <code>boolean</code>: Whether or not the directory already existed.
     * @throws IOException Thrown if the designated directory cannot be created for
     *                     any reason.
     */
    public boolean createDirectory() throws IOException {

        if (isDirectory()) {

            return true;
        }

        Files.createDirectory(path);
        return false;
    }

    /**
     * Writes a <code>String</code> the file pointed to by this
     * <code>ExternalPath</code> instance.
     * 
     * @param contents <code>String</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for
     *                     any reason.
     */
    public void writeTo(String contents) throws IOException {

        createFile();
        Files.write(path, contents.getBytes());
    }

    /**
     * Writes a <code>ByteBuffer</code> the file pointed to by this
     * <code>ExternalPath</code> instance.
     * 
     * @param contents <code>ByteBuffer</code>: The buffer to write.
     * @throws IOException Thrown if the designated file cannot be written to for
     *                     any reason.
     */
    public void writeTo(ByteBuffer contents) throws IOException {

        createFile();
        Files.write(path, contents.array());
    }

    @Override
    public ExternalPath extend(String... next) {

        Stream<String> components = getComponents().stream();
        String[] pathway = Stream.concat(components, Arrays.stream(next)).toArray(String[]::new);

        return new ExternalPath(pathway);
    }

    /**
     * Retrieves the file pointed to by this <code>ExternalPath</code> instance.
     * 
     * @return <code>File</code>: The resulting <code>File</code> instance.
     */
    private File getFile() {

        return path.toFile();
    }

    @Override
    public boolean exists() {

        return getFile().exists();
    }

    @Override
    public boolean isFile() {

        return getFile().isFile();
    }

    @Override
    public boolean isDirectory() {

        return getFile().isDirectory();
    }

    @Override
    public InputStream getInputStream() {

        try {
            return Files.newInputStream(path);

        } catch (IOException e) {

            return null;
        }
    }

    @Override
    public long getSize() {

        if (isFile()) {

            return getFile().length();
        } else if (isDirectory()) {

            long sum = 0;
            for (TracedPath pathway : compileEntries()) {

                sum += pathway.getSize();
            }

            return sum;
        }

        return -1;
    }

    @Override
    public List<ExternalPath> compileEntries() {

        File[] files = getFile().listFiles();

        if (files == null)
            return new ArrayList<>();

        Stream<File> fileStream = List.of(files).stream();

        return fileStream.map(file -> extend(file.getName())).toList();
    }
}
