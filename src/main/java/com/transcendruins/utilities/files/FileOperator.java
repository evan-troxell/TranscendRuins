package com.transcendruins.utilities.files;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * A set of operation methods to parse and process file information.
 */
public final class FileOperator {

    /**
     * <code>TracedPath</code>: The filepath of the project build.
     */
    public static final TracedPath LOCAL_ROOT_DIRECTORY = new TracedPath(Path.of(System.getProperty("user.dir")));

    /**
     * <code>TracedPath</code>: The filepath of the home directory.
     */
    public static final TracedPath HOME_DIRECTORY = new TracedPath(Path.of(System.getProperty("user.home")));

    /**
     * Writes a string to the designated file from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The path to write to.
     * @param contents <code>String</code>: The contents to write.
     * @throws IOException Thrown if the designated file cannot be written to for any reason.
     */
    public static void writeTo(TracedPath path, String contents) throws IOException {

        createFile(path, false);
        Files.write(path.getPath(), contents.getBytes());
    }

    /**
     * Writes a <code>ByteBuffer</code> to the designated directory from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The path to write to.
     * @param contents <code>ByteBuffer</code>: The buffer to write.
     * @throws IOException Thrown if the designated file cannot be written to for any reason.
     */
    public static void writeTo(TracedPath path, ByteBuffer contents) throws IOException {

        createFile(path, false);
        Files.write(path.getPath(), contents.array());
    }

    /**
     * Retrieves the file contents using the given filepath from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The file to search for.
     * @return <code>String</code>: The retrieved file information.
     */
    public static String retrieve(TracedPath path) {

        if (!exists(path)) {

            return null;
        }
        try {

            String contents = new String(Files.readAllBytes(path.getPath()));
            return contents;
        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Retrieves an image using the given filepath from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The image to search for.
     * @return <code>ImageIcon</code>: The retrieved image.
     */
    public static ImageIcon retrieveImage(TracedPath path) {

        if (!exists(path)) {

            return null;
        }
        ImageIcon image = new ImageIcon(path.toString());

        return image;
    }

    /**
     * Returns whether or not a given filepath is a file (or alternatively a directory) from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The file to search for.
     * @param isDirectory <code>boolean</code>: Whether or not to check for a directory instead.
     * @return <code>boolean</code>: Whether or not the path ends in a file or directory.
     */
    public static boolean isFile(TracedPath path, boolean isDirectory) {

        File file = path.getPath().toFile();
        return isDirectory ? file.isDirectory() : file.isFile();
    }

    /**
     * Determines whether or not a file exists from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The file or directory to search for.
     * @return <code>boolean</code>: Whether or not the file or directory exists.
     */
    public static boolean exists(TracedPath path) {

        File file = path.getPath().toFile();
        return file.exists();
    }

    /**
     * Creates a new file from a <code>TracedPath</code> directory.
     * @param path <code>TracedPath</code>: The directory to create from.
     * @param isDirectory <code>boolean</code>: Whether or not to create a directory instead.
     * @return <code>boolean</code>: Whether or not the file already existed.
     * @throws IOException Thrown if the designated directory cannot be created for any reason.
     */
    public static boolean createFile(TracedPath path, boolean isDirectory) throws IOException {

        if (exists(path)) {

            return true;
        }
        
        if (isDirectory) {
            
            Files.createFile(path.getPath());
        } else {

            Files.createDirectory(path.getPath());
        }
        return false;
    }

    /**
     * Constructs a list of files contained within a root directory.
     * @param root <code>TracedPath</code>: The root directory to construct from.
     * @param endsWith <code>String</code>: A string with which all compiled directories must end with. If null, this will be ignored.
     * @param recursive <code>boolean</code>: Whether or not files in subsequent child directories should be recursively checked.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of <code>TracedPath</code> corresponding to files within the root directory.
     */
    public static ArrayList<TracedPath> compileFileArray(TracedPath root, String endsWith, boolean recursive) {

        ArrayList<TracedPath> paths = new ArrayList<>();
        ArrayList<TracedPath> pathsUnprocessed = new ArrayList<>();
        if (exists(root)) {

            pathsUnprocessed.add(root);
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
     * Constructs a list of directories contained within a root directory. This will NOT recursively check subsequent child directories.
     * @param root <code>TracedPath</code>: The root directory to construct from.
     * @param endsWith <code>String</code>: A string with which all compiled directories must end with. If null, this will be ignored.
     * @return <code>ArrayList&lt;TracedPath&gt;</code>: A list of <code>TracedPath</code> corresponding to directories within the root directory.
     */
    public static ArrayList<TracedPath> compileDirectoryArray(TracedPath root, String endsWith) {

        ArrayList<TracedPath> paths = new ArrayList<>();
        if (!exists(root) || isFile(root, false)) {

            return paths;
        }

        File[] newFiles = root.getPath().toFile().listFiles();

        for (File path : newFiles) {

            if (endsWith != null && !path.getName().endsWith(endsWith)) {

                continue;
            }

            TracedPath newPath = root.extend(path.getName());
            if (isFile(newPath, true)) {

                paths.add(newPath);
            }
        }

        return paths;
    }

    /**
     * Prevents the <code>FileOperator</code> class from being instantiated.
     */
    private FileOperator() {}
}
