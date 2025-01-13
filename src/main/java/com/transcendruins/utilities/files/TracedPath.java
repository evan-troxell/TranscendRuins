package com.transcendruins.utilities.files;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * <code>TracedPath</code>: A class representing a filepath which has been
 * traced, and which can be inherited from.
 */
public final class TracedPath {

    /**
     * <code>TracedPath</code>: The filepath of the project build.
     */
    public static final TracedPath LOCAL_ROOT_DIRECTORY = new TracedPath(Path.of(System.getProperty("user.dir")));

    /**
     * <code>TracedPath</code>: The filepath of the internal directory.
     */
    public static final TracedPath INTERNAL_DIRECTORY = LOCAL_ROOT_DIRECTORY.extend("internal");

    /**
     * <code>TracedPath</code>: The filepath of the home directory.
     */
    public static final TracedPath HOME_DIRECTORY = new TracedPath(Path.of(System.getProperty("user.home")));

    /**
     * <code>Path</code>: The directory this path is inherited from.
     */
    private final Path path;

    /**
     * Retrieves the path pointed to by this <code>TracedPath</code> instance.
     * 
     * @return <code>Path</code>: The <code>path</code> field of this <code>TracedPath</code> instance.
     */
    public Path getPath() {

        return path;
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * 
     * @param root <code>Path</code>: The root directory to initiate this
     *             <code>TracedPath</code> from.
     */
    public TracedPath(Path root) {

        path = root.toAbsolutePath();
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * 
     * @param root       <code>TracedPath</code>: The root <code>TracedPath</code>
     *                   to initiate this <code>TracedPath</code> from.
     * @param pathString <code>String...</code>: The path to extend along.
     */
    private TracedPath(TracedPath root, String... pathString) {

        this.path = Path.of(root.path.toString(), pathString);
    }

    /**
     * Extends this <code>TracedPath</code> object along a directory branch.
     * 
     * @param pathString <code>String...</code>: The path to extend along.
     * @return <code>TracedPath</code>: The generated <code>TracedPath</code>
     *         instance.
     */
    public TracedPath extend(String... pathString) {

        return new TracedPath(this, pathString);
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
    public String retrieveContents() {

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

        return image;
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

        File file = getPath().toFile();
        return isDirectory ? file.isDirectory() : file.isFile();
    }

    /**
     * Retrieves whether or not this <code>TracedPath</code> instance points to a
     * valid file (or directory).
     * 
     * @return <code>boolean</code>: Whether or not the file or directory exists.
     */
    public boolean exists() {

        File file = getPath().toFile();
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
     * @param endsWith  <code>String</code>: A string with which all compiled
     *                  directories must end with. If null, this will be ignored.
     * @param recursive <code>boolean</code>: Whether or not files in subsequent
     *                  child directories should be recursively checked.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> corresponding to files within the root
     *         directory.
     */
    public ArrayList<TracedPath> compileFileArray(String endsWith, boolean recursive) {

        ArrayList<TracedPath> paths = new ArrayList<>();
        ArrayList<TracedPath> pathsUnprocessed = new ArrayList<>();
        if (exists()) {

            pathsUnprocessed.add(this);
        }

        while (!pathsUnprocessed.isEmpty()) {

            TracedPath currentPath = pathsUnprocessed.get(0);
            pathsUnprocessed.remove(0);

            File pathFile = currentPath.getPath().toFile();
            if (pathFile.isFile()) {

                if (endsWith != null && !pathFile.getName().endsWith(endsWith)) {

                    continue;
                }
                paths.add(currentPath);
            } else {

                if (!recursive) {

                    continue;
                }

                File[] newFiles = pathFile.listFiles();
                ArrayList<TracedPath> newFilesList = new ArrayList<>();

                for (File file : newFiles) {

                    newFilesList.add(currentPath.extend(file.getName()));
                }

                pathsUnprocessed.addAll(newFilesList);
            }
        }

        return paths;
    }

    /**
     * Constructs a list of directories contained within this
     * <code>TracedPath</code> instance. This will NOT recursively check subsequent
     * child directories.
     * 
     * @param endsWith <code>String</code>: A string with which all compiled
     *                 directories must end with. If null, this will be ignored.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of
     *         <code>TracedPath</code> corresponding to directories within the root
     *         directory.
     */
    public ArrayList<TracedPath> compileDirectoryArray(String endsWith) {

        ArrayList<TracedPath> paths = new ArrayList<>();
        if (!exists() || isFile(false)) {

            return paths;
        }

        File[] newFiles = getPath().toFile().listFiles();

        for (File subPath : newFiles) {

            if (endsWith != null && !subPath.getName().endsWith(endsWith)) {

                continue;
            }

            TracedPath newPath = extend(subPath.getName());
            if (newPath.isFile(true)) {

                paths.add(newPath);
            }
        }

        return paths;
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
}
