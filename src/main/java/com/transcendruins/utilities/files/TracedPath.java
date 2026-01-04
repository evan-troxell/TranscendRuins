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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.sound.StoredSound;

/**
 * <code>TracedPath</code>: A class representing a filepath which has been
 * traced, and which can be inherited from.
 */
public abstract sealed class TracedPath permits InternalPath, ExternalPath {

    /**
     * <code>int</code>: An enum constant representing an unrepresented file type.
     */
    public static final int OTHER = 0;

    /**
     * <code>int</code>: An enum constant representing an image file compatible with
     * the built-in Java API.
     */
    public static final int IMAGE = 1;

    /**
     * <code>int</code>: An enum constant representing an audio file compatible with
     * the built-in Java API.
     */
    public static final int AUDIO = 2;

    /**
     * <code>int</code>: An enum constant representing a JSON file.
     */
    public static final int JSON = 3;

    /**
     * <code>int</code>: An enum constant representing a text file.
     */
    public static final int TEXT = 4;

    /**
     * <code>int</code>: An enum constant representing a content pack file.
     */
    public static final int CONTENT_PACK = 5;

    /**
     * <code>int</code>: An enum constant representing a resource pack file.
     */
    public static final int RESOURCE_PACK = 6;

    /**
     * <code>String</code>: The file separator used on the current operating system.
     */
    public static String SEPARATOR = File.separator;

    /**
     * <code>String[]</code>: The list of filesize units from bytes to terabytes.
     */
    private static final String[] UNITS = { "bytes", "kilobytes", "megabytes", "gigabytes", "terabytes" };

    /**
     * <code>ClassLoader</code>: The class loader of the java program.
     */
    protected static final ClassLoader CLASS_LOADER = TracedPath.class.getClassLoader();

    /**
     * Retrieves a resource from the classpath.
     * 
     * @param path <code>String</code>: The path to the resource.
     * @return <code>URL</code>: The URL of the resource.
     */
    protected static final URL getResource(String path) {

        return CLASS_LOADER.getResource(path);
    }

    /**
     * Retrieves a resource stream from the classpath.
     * 
     * @param path <code>String</code>: The path to the resource.
     * @return <code>InputStream</code>: The input stream of the resource.
     */
    protected static final InputStream getResourceAsStream(String path) {

        return CLASS_LOADER.getResourceAsStream(path);
    }

    public static final InternalPath INTERNAL_DIRECTORY = InternalPath.ROOT.extend("assets");

    /**
     * <code>ExternalPath</code>: The filepath of the home directory.
     */
    public static final ExternalPath HOME_DIRECTORY = createExternalPath(System.getProperty("user.home"));

    public static final ExternalPath LIBRARY_DIRECTORY;

    static {

        LIBRARY_DIRECTORY = HOME_DIRECTORY.extend("Transcend Ruins Library");

        try {

            LIBRARY_DIRECTORY.createDirectory();
        } catch (IOException e) {

            System.out.println("Library directory could not be generated. Proceeding...");
        }
    }

    /**
     * <code>boolean</code>: Whether or not this <code>TracedPath</code> instance is
     * an internal path.
     */
    private final boolean internal;

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance is an internal
     * path.
     * 
     * @return <code>boolean</code>: The <code>internal</code> field of this
     *         <code>TracedPath</code> instance.
     */
    public final boolean isInternal() {

        return internal;
    }

    /**
     * <code>ImmutableList&lt;String&gt;</code>: The components of this
     * <code>TracedPath</code> instance.
     */
    private final ImmutableList<String> components;

    /**
     * Retrieves the components of this <code>TracedPath</code> instance.
     * 
     * @return <code>ImmutableList&lt;String&gt;</code>: A the
     *         <code>components</code> field of this <code>TracedPath</code>
     *         instance.
     */
    public final ImmutableList<String> getComponents() {

        return components;
    }

    /**
     * <code>String</code>: The filename pointed to by this <code>TracedPath</code>
     * instance.
     */
    private final String fileName;

    /**
     * Retrieves the filename pointed to by this <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The filename of the <code>path</code> field of
     *         this <code>TracedPath</code> instance.
     */
    public final String getFileName() {

        return fileName;
    }

    /**
     * <code>String</code>: The file stem (filename without extension) pointed to by
     * this <code>TracedPath</code> instance.
     */
    private final String fileStem;

    /**
     * Retrieves the file stem (filename without extension) pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The file stem of the <code>path</code> field of
     *         this <code>TracedPath</code> instance.
     */
    public final String getFileStem() {

        return fileStem;
    }

    /**
     * <code>String</code>: The file extension of this <code>TracedPath</code>
     * instance.
     */
    private final String extension;

    /**
     * Retrieves the file extension of this <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The <code>extension</code> field of this
     *         <code>TracedPath</code> instance.
     */
    public String getExtension() {

        return extension;
    }

    /**
     * Retrieves the file type of this <code>TracedPath</code> instance.
     * 
     * @return <code>int</code>: The file type of this <code>TracedPath</code>
     *         instance represented by an <code>int</code> enum.
     */
    public final int getFileType() {

        return switch (extension) {

            case ".txt" -> TEXT;

            case ".json" -> JSON;

            case ".jpg", ".jpeg", ".png", ".gif" -> {

                yield isValidImage() ? IMAGE : OTHER;
            }

            case ".wav", ".au", ".aiff" -> {

                yield isValidAudio() ? AUDIO : OTHER;
            }

            case ".contentpack" -> CONTENT_PACK;

            case ".resourcepack" -> RESOURCE_PACK;

            default -> OTHER;
        };
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * 
     * @param internal   <code>boolean</code>: Whether or not the created
     *                   <code>TracedPath</code> instance should be an internal
     *                   path.
     * @param components <code>String...</code>: The path components to initiate
     *                   along.
     * @return <code>TracedPath</code>: The generated <code>TracedPath</code>
     *         instance.
     */
    public static final TracedPath createPath(boolean internal, String... components) {

        if (internal) {

            return createInternalPath(components);
        } else {

            return createExternalPath(components);
        }
    }

    /**
     * Creates a new instance of the <code>InternalPath</code> class.
     * 
     * @param components <code>String...</code>: The path components to initiate
     *                   along.
     * @return <code>InternalPath</code>: The generated <code>InternalPath</code>
     *         instance.
     */
    public static final InternalPath createInternalPath(String... components) {

        return InternalPath.createPath(components);
    }

    /**
     * Creates a new instance of the <code>ExternalPath</code> class.
     * 
     * @param components <code>String...</code>: The path components to initiate
     *                   along.
     * @return <code>ExternalPath</code>: The generated <code>ExternalPath</code>
     *         instance.
     */
    public static final ExternalPath createExternalPath(String... components) {

        return new ExternalPath(components);
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * 
     * @param internal   <code>boolean</code>: Whether or not this this
     *                   <code>TracedPath</code> instance is an internal path.
     * @param components <code>String...</code>: The path to initiate along.
     */
    protected TracedPath(boolean internal, String... components) {

        this.internal = internal;
        ArrayList<String> componentList = new ArrayList<>();

        for (String component : components) {

            // Convert any native separators into slashes for simplicity.
            component = component.replaceAll(SEPARATOR, "/");

            // Remove preceding slashes (for normalization).
            if (component.startsWith("/")) {

                component = component.replaceFirst("^/+", "");
            }

            // Remove succeeding slashes (for normalization).
            if (component.endsWith("/")) {

                component = component.replaceFirst("/+$", "");
            }

            // If there are any slashes in the middle, separate into individual components.
            String[] subComponents = component.split("/");

            // Add each component.
            Collections.addAll(componentList, subComponents);
        }

        this.components = new ImmutableList<>(componentList);

        fileName = !componentList.isEmpty() ? componentList.getLast() : "";

        int dotIndex = fileName.lastIndexOf(".");
        fileStem = (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;

        extension = (dotIndex != -1) ? fileName.substring(dotIndex).toLowerCase() : "";
    }

    /**
     * Extends this <code>TracedPath</code> object along a directory branch.
     * 
     * @param next <code>String...</code>: The path to extend along.
     * @return <code>TracedPath</code>: The generated <code>TracedPath</code>
     *         instance.
     */
    public abstract TracedPath extend(String... next);

    /**
     * Determines whether or not this <code>TracedPath</code> instance begins with
     * the provided <code>TracedPath</code> instance.
     * 
     * @param startAt <code>TracedPath</code>: The path to check against.
     * @return <code>boolean</code>: Whether or not this <code>TracedPath</code>
     *         instance begins with the provided <code>TracedPath</code> instance.
     */
    public final boolean beginsWith(TracedPath startAt) {

        if (startAt == null || internal != startAt.internal || components.size() < startAt.getComponents().size()) {

            return false;
        }

        for (int i = 0; i < startAt.getComponents().size(); i++) {

            if (!components.get(i).equals(startAt.getComponents().get(i))) {

                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * valid file (or directory).
     * 
     * @return <code>boolean</code>: Whether or not the file or directory exists.
     */
    public abstract boolean exists();

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * file.
     * 
     * @return <code>boolean</code>: Whether or not the endpoint is a file.
     */
    public abstract boolean isFile();

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * directory.
     * 
     * @return <code>boolean</code>: Whether or not the endpoint is a directory.
     */
    public abstract boolean isDirectory();

    /**
     * Retrieves the input stream of the file pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>InputStream</code>: The input stream of the file pointed to by
     *         this <code>TracedPath</code> instance.
     */
    public abstract InputStream getInputStream();

    /**
     * Retrieves the bytes of the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>byte[]</code>: The retrieved bytes.
     */
    public final byte[] getBytes() {

        try (InputStream inputStream = getInputStream()) {

            if (inputStream == null) {

                return null;
            }

            return inputStream.readAllBytes();
        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Retrieves the size of the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>long</code>: The size of the file, or <code>-1</code> if the
     *         file could not be found.
     */
    public abstract long getSize();

    public static final String formatSize(long size) {

        int pow = (size > 0) ? (int) (Math.log(size) / Math.log(1000)) : 0;
        if (pow >= UNITS.length) {

            pow = UNITS.length - 1;
        }

        double shifted = size / Math.pow(1000, pow);

        String unit = UNITS[pow];
        String num = String.format((shifted % 1 == 0) ? "%.0f" : "%.2f", shifted);

        return num + " " + unit;
    }

    public final String getFormattedSize() {

        long bytes = getSize();

        return formatSize(bytes);
    }

    /**
     * Retrieves the file contents from the file pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The retrieved file information.
     */
    public final String retrieve() {

        if (!exists()) {

            return null;
        }
        try (InputStream inputStream = getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader)) {

            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Determines whether or not the file pointed to by this <code>TracedPath</code>
     * instance is a valid image.
     * 
     * @return <code>boolean</code>: Whether or not the image retrieved by this
     *         <code>TracedPath</code> is a valid image.
     */
    public final boolean isValidImage() {

        return retrieveImage() != null;
    }

    /**
     * Retrieves an image from the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>ImageIcon</code>: The retrieved image.
     */
    public final ImageIcon retrieveImage() {

        if (!exists()) {

            return null;
        }
        try (InputStream inputStream = getInputStream()) {

            ImageIcon image = new ImageIcon(ImageIO.read(inputStream));
            return (image.getIconWidth() > 0 && image.getIconHeight() > 0) ? image : null;
        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Determines whether or not the file pointed to by this <code>TracedPath</code>
     * instance is a valid audio.
     * 
     * @return <code>boolean</code>: Whether or not the audio retrieved by this
     *         <code>TracedPath</code> is a valid audio.
     */
    public final boolean isValidAudio() {

        return retrieveAudio() != null;
    }

    /**
     * Retrieves an audio from the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>StoredSound</code>: The retrieved audio.
     */
    public final StoredSound retrieveAudio() {

        try {

            return new StoredSound(this);
        } catch (UnsupportedAudioFileException | IOException e) {

            return null;
        }
    }

    /**
     * Constructs a list of all of the immediate entries contained within this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>Collection&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to the immediate
     *         files and directories within the root directory.
     */
    protected abstract List<? extends TracedPath> compileEntries();

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>Collection&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<? extends TracedPath> listFiles() {

        return compileEntries().stream().filter(TracedPath::isFile).toList();
    }

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @param fileType <code>int</code>: The file type to match each file to.
     * @return <code>List&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<? extends TracedPath> listFiles(int fileType) {

        return listFiles().stream().filter(n -> n.getFileType() == fileType).toList();
    }

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @param regex <code>String</code>: The regular expression to match each
     *              filename to.
     * @return <code>List&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<? extends TracedPath> listFiles(String regex) {

        return listFiles().stream().filter(n -> n.getFileName().matches(regex)).toList();
    }

    /**
     * Recursively constructs a list of files contained within this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>Collection&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<TracedPath> listRecursiveFiles() {

        return compileEntries().stream()
                .flatMap(file -> file.isDirectory() ? file.listRecursiveFiles().stream() : Stream.of(file)).toList();
    }

    /**
     * Recursively constructs a list of files contained within this
     * <code>TracedPath</code> instance.
     * 
     * @param fileType <code>int</code>: The file type to match each file to.
     * @return <code>List&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<TracedPath> listRecursiveFiles(int fileType) {

        return compileEntries().stream()
                .flatMap(file -> file.isDirectory() ? file.listRecursiveFiles().stream() : Stream.of(file))
                .filter(n -> n.getFileType() == fileType).toList();
    }

    /**
     * Recursively constructs a list of files contained within this
     * <code>TracedPath</code> instance.
     * 
     * @param regex <code>String</code>: The regular expression to match each
     *              filename to.
     * @return <code>List&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public final List<TracedPath> listRecursiveFiles(String regex) {

        return compileEntries().stream()
                .flatMap(file -> file.isDirectory() ? file.listRecursiveFiles().stream() : Stream.of(file))
                .filter(n -> n.getFileName().matches(regex)).toList();
    }

    /**
     * Constructs a recursive list of the immediate directories contained within
     * this <code>TracedPath</code> instance.
     * 
     * @return <code>Collection&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to the immediate
     *         directories within the root directory.
     */
    public final List<? extends TracedPath> listDirectories() {

        return compileEntries().stream().filter(TracedPath::isDirectory).toList();
    }

    /**
     * Constructs a recursive list of the immediate directories contained within
     * this <code>TracedPath</code> instance.
     * 
     * @param fileType <code>int</code>: The file type to match each file to.
     * @return <code>List&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to the immediate
     *         directories within the root directory.
     */
    public final List<? extends TracedPath> listDirectories(int fileType) {

        return listDirectories().stream().filter(n -> n.getFileType() == fileType).toList();
    }

    /**
     * Constructs a recursive list of the immediate directories contained within
     * this <code>TracedPath</code> instance.
     * 
     * @param regex <code>String</code>: The regular expression to match each
     *              filename to.
     * @return <code>List&lt;? extends TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to the immediate
     *         directories within the root directory.
     */
    public final List<? extends TracedPath> listDirectories(String regex) {

        return listDirectories().stream().filter(n -> n.getFileName().matches(regex)).toList();
    }

    /**
     * Returns the string representation of this <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: This <code>TracedPath</code> instance in the
     *         following string representation: <br>
     *         "<code>path/to/file.extension</code>"
     */
    @Override
    public final String toString() {

        return String.join("/", components);
    }

    public final String toString(TracedPath startAt) {

        if (startAt == null || !beginsWith(startAt)) {

            return toString();
        }

        ArrayList<String> relativeComponents = new ArrayList<>(components);
        for (String _ : startAt.getComponents()) {

            relativeComponents.remove(0);
        }

        return String.join("/", relativeComponents);
    }

    @Override
    public final int hashCode() {

        return toString().hashCode();
    }

    @Override
    public final boolean equals(Object other) {

        if (other == null || other.getClass() != getClass()) {

            return false;
        }

        return toString().equals(other.toString());
    }
}
