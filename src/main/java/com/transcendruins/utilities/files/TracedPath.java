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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import com.transcendruins.utilities.sound.StoredSound;

/**
 * <code>TracedPath</code>: A class representing a filepath which has been
 * traced, and which can be inherited from.
 */
public final class TracedPath {

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
     * <code>int</code>: An enum constant representing a sound file compatible with
     * the built-in Java API.
     */
    public static final int SOUND = 2;

    /**
     * <code>int</code>: An enum constant representing a JSON file.
     */
    public static final int JSON = 3;

    /**
     * <code>int</code>: An enum constant representing a text file.
     */
    public static final int TEXT = 4;

    /**
     * <code>int</code>: An enum constant representing a pack file.
     */
    public static final int PACK = 5;

    /**
     * <code>int</code>: An enum constant representing a resource file.
     */
    public static final int RESOURCE = 6;

    /**
     * <code>TracedPath</code>: The filepath of the project build.
     */
    public static final TracedPath LOCAL_ROOT_DIRECTORY = new TracedPath(System.getProperty("user.dir"));

    /**
     * <code>TracedPath</code>: The filepath of the game directory.
     */
    public static final TracedPath GAME_DIRECTORY = LOCAL_ROOT_DIRECTORY.extend("game");

    /**
     * <code>TracedPath</code>: The filepath of the data directory.
     */
    public static final TracedPath DATA_DIRECTORY = GAME_DIRECTORY.extend("data");

    /**
     * <code>TracedPath</code>: The filepath of the custom assets directory.
     */
    public static final TracedPath CUSTOM_DIRECTORY = DATA_DIRECTORY.extend("custom");

    /**
     * <code>TracedPath</code>: The filepath of the internal assets directory.
     */
    public static final TracedPath INTERNAL_DIRECTORY = DATA_DIRECTORY.extend("internal");

    /**
     * <code>TracedPath</code>: The filepath of the home directory.
     */
    public static final TracedPath HOME_DIRECTORY = new TracedPath(System.getProperty("user.home"));

    /**
     * <code>String</code>: The standard file separator.
     */
    public static final String SEPARATOR = System.getProperty("file.separator");

    /**
     * <code>Path</code>: The directory this path is inherited from.
     */
    private final Path path;

    /**
     * Retrieves the path pointed to by this <code>TracedPath</code> instance.
     * 
     * @return <code>Path</code>: The <code>path</code> field of this
     *         <code>TracedPath</code> instance.
     */
    public Path getPath() {

        return path;
    }

    /**
     * Retrieves the filename pointed to by this <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The filename of the <code>path</code> field of
     *         this <code>TracedPath</code> instance.
     */
    public String getFileName() {

        return path.getFileName().toString();
    }

    public String getFileStem() {

        String fileName = getFileName();
        int dotIndex = fileName.lastIndexOf(".");

        return (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;
    }

    /**
     * Retrieves the file extension pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>String</code>: The file extension of the <code>path</code>
     *         field of
     *         this <code>TracedPath</code> instance.
     */
    public String getFileExtension() {

        String fileName = getFileName();
        int dotIndex = fileName.lastIndexOf(".");

        return (dotIndex != -1) ? fileName.substring(dotIndex) : "";
    }

    /**
     * Retrieves the file type of this <code>TracedPath</code> instance.
     * 
     * @return <code>int</code>: The file type of this <code>TracedPath</code>
     *         instance represented by an <code>int</code> enum.
     */
    public int getFileType() {

        String extension = getFileExtension().toLowerCase();

        if (extension.isEmpty()) {

            return OTHER;
        }

        if (extension.equals(".txt")) {

            return TEXT;
        }

        if (extension.equals(".json")) {

            return JSON;
        }

        if (extension.matches("\\.(jpg|jpeg|png|gif)")) {

            if (isValidImage()) {

                return IMAGE;
            }
        }

        if (extension.matches("\\.(wav|au|aiff)")) {

            if (isValidSound()) {

                return SOUND;
            }
        }

        if (extension.equals(".pack")) {

            return PACK;
        }

        if (extension.equals(".resource")) {

            return RESOURCE;
        }

        return OTHER;
    }

    /**
     * Retrieves the size of the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>long</code>: The size of the file, or <code>-1</code> if the
     *         file could not be found.
     */
    public long getSize() {

        try {

            return Files.size(path);
        } catch (IOException e) {

            return -1;
        }
    }

    /**
     * Retrieves the bytes of the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>byte[]</code>: The retrieved bytes.
     * @throws IOException Thrown if the file could not be processed.
     */
    public byte[] getBytes() throws IOException {

        return Files.readAllBytes(path);
    }

    /**
     * Retrieves the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>File</code>: The retrieved file.
     */
    public File toFile() {

        return path.toFile();
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * 
     * @param root <code>String</code>: The root directory to initiate this
     *             <code>TracedPath</code> from.
     * @param next <code>String...</code>: The path to initiate along.
     */
    public TracedPath(String root, String... next) {

        Path combined = Path.of(root);

        for (String component : next) {

            combined = combined.resolve(component).normalize();
        }

        path = combined;
    }

    /**
     * Extends this <code>TracedPath</code> object along a directory branch.
     * 
     * @param next <code>String...</code>: The path to extend along.
     * @return <code>TracedPath</code>: The generated <code>TracedPath</code>
     *         instance.
     */
    public TracedPath extend(String... next) {

        return new TracedPath(toString(), next);
    }

    /**
     * Writes a <code>String</code> the file pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @param contents <code>String</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for
     *                     any reason.
     */
    public void writeTo(String contents) throws IOException {

        createFile(false);
        Files.write(getPath(), contents.getBytes());
    }

    /**
     * Writes a <code>ByteBuffer</code> the file pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @param contents <code>ByteBuffer</code>: The buffer to write.
     * @throws IOException Thrown if the designated file cannot be written to for
     *                     any reason.
     */
    public void writeTo(ByteBuffer contents) throws IOException {

        createFile(false);
        Files.write(getPath(), contents.array());
    }

    /**
     * Retrieves the file contents from the file pointed to by this
     * <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: The retrieved file information.
     */
    public String retrieve() {

        if (!exists()) {

            return null;
        }
        try {

            String contents = new String(Files.readAllBytes(getPath()));
            return contents;
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
    public boolean isValidImage() {

        return retrieveImage() != null;
    }

    /**
     * Retrieves an image from the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>ImageIcon</code>: The retrieved image.
     */
    public ImageIcon retrieveImage() {

        if (!exists()) {

            return null;
        }
        ImageIcon image = new ImageIcon(toString());

        return (image.getIconWidth() > 0 && image.getIconHeight() > 0) ? image : null;
    }

    /**
     * Determines whether or not the file pointed to by this <code>TracedPath</code>
     * instance is a valid image.
     * 
     * @return <code>boolean</code>: Whether or not the image retrieved by this
     *         <code>TracedPath</code> is a valid image.
     */
    public boolean isValidSound() {

        return retrieveSound() != null;
    }

    /**
     * Retrieves a sound from the file pointed to by this <code>TracedPath</code>
     * instance.
     * 
     * @return <code>StoredSound</code>: The retrieved sound.
     */
    public StoredSound retrieveSound() {

        try {

            return new StoredSound(this);
        } catch (UnsupportedAudioFileException | IOException e) {

            return null;
        }
    }

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * file (or alternatively a directory).
     * 
     * @param isDirectory <code>boolean</code>: Whether or not to check for a
     *                    directory instead.
     * @return <code>boolean</code>: Whether or not the path ends in a file or
     *         directory.
     */
    public boolean isFile(boolean isDirectory) {

        File file = toFile();
        return isDirectory ? file.isDirectory() : file.isFile();
    }

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * valid file (or directory).
     * 
     * @return <code>boolean</code>: Whether or not the file or directory exists.
     */
    public boolean exists() {

        File file = toFile();
        return file.exists();
    }

    /**
     * Creates a new file (or directory) from the path of this
     * <code>TracedPath</code> instance.
     * 
     * @param isDirectory <code>boolean</code>: Whether or not to create a directory
     *                    instead.
     * @return <code>boolean</code>: Whether or not the file already existed.
     * @throws IOException Thrown if the designated directory cannot be created for
     *                     any reason.
     */
    public boolean createFile(boolean isDirectory) throws IOException {

        if (exists()) {

            return true;
        }

        if (isDirectory) {

            Files.createDirectory(getPath());
        } else {

            Files.createFile(getPath());
        }
        return false;
    }

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @param recursive <code>boolean</code>: Whether or not files in subsequent
     *                  child directories should be recursively checked.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public ArrayList<TracedPath> compileFiles(boolean recursive) {

        ArrayList<TracedPath> paths = new ArrayList<>();

        if (!exists()) {

            return paths;
        }

        if (isFile(true) && recursive) {

            for (File file : toFile().listFiles()) {

                TracedPath subPath = extend(file.getName());
                paths.addAll(subPath.compileFiles(recursive));
            }
        } else {

            paths.add(this);
        }

        return paths;
    }

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @param fileType  <code>int</code>: The file type to match each file to.
     * @param recursive <code>boolean</code>: Whether or not files in subsequent
     *                  child directories should be recursively checked.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public ArrayList<TracedPath> compileFiles(int fileType, boolean recursive) {

        return new ArrayList<>(compileFiles(recursive)
                .stream()
                .filter(n -> n.getFileType() == fileType)
                .collect(Collectors.toList()));
    }

    /**
     * Constructs a list of files contained within this <code>TracedPath</code>
     * instance.
     * 
     * @param regex     <code>String</code>: The regular expression to match each
     *                  filename to.
     * @param recursive <code>boolean</code>: Whether or not files in subsequent
     *                  child directories should be recursively checked.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to files within the
     *         root directory.
     */
    public ArrayList<TracedPath> compileFiles(String regex, boolean recursive) {

        return new ArrayList<>(compileFiles(recursive)
                .stream()
                .filter(n -> n.getFileName().matches(regex))
                .collect(Collectors.toList()));
    }

    /**
     * Constructs a list of directories contained within this
     * <code>TracedPath</code> instance. This will NOT recursively check subsequent
     * child directories.
     * 
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to directories within
     *         the root
     *         directory.
     */
    public ArrayList<TracedPath> compileDirectories() {

        ArrayList<TracedPath> paths = new ArrayList<>();
        if (!isFile(true)) {

            return paths;
        }

        File[] newFiles = toFile().listFiles();

        for (File subPath : newFiles) {

            TracedPath newPath = extend(subPath.getName());
            if (newPath.isFile(true)) {

                paths.add(newPath);
            }
        }

        return paths;
    }

    /**
     * Constructs a list of directories contained within this
     * <code>TracedPath</code> instance. This will NOT recursively check subsequent
     * child directories.
     * 
     * @param fileType <code>int</code>: The file type to match each directory to.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to directories within
     *         the root
     *         directory.
     */
    public ArrayList<TracedPath> compileDirectories(int fileType) {

        return new ArrayList<>(compileDirectories()
                .stream()
                .filter(n -> n.getFileType() == fileType)
                .collect(Collectors.toList()));
    }

    /**
     * Constructs a list of directories contained within this
     * <code>TracedPath</code> instance. This will NOT recursively check subsequent
     * child directories.
     * 
     * @param regex <code>String</code>: The regular expression to match each
     *              filename to.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> instances corresponding to directories within
     *         the root
     *         directory.
     */
    public ArrayList<TracedPath> compileDirectories(String regex) {

        return new ArrayList<>(compileDirectories()
                .stream()
                .filter(n -> n.getFileName().matches(regex))
                .collect(Collectors.toList()));
    }

    /**
     * Returns the string representation of this <code>TracedPath</code> instance.
     * 
     * @return <code>String</code>: This <code>TracedPath</code> instance in the
     *         following string representation: <br>
     *         "<code>absolute/pathway/example.txt</code>"
     */
    @Override
    public String toString() {

        return getPath().toString();
    }

    @Override
    public int hashCode() {

        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof TracedPath)) {

            return false;
        }

        if (this == obj) {

            return true;
        }

        return path.equals(obj);
    }
}
