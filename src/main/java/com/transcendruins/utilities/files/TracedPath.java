package com.transcendruins.utilities.files;

import java.nio.file.Path;

/**
 * <code>TracedPath</code>: A class representing a filepath which has been traced, and which can be inherited from.
 */
public final class TracedPath {

    /**
     * <code>String</code>: The directory from this path is inherited from.
     */
    public final String path;

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * @param root <code>Path</code>: The root directory to initiate this <code>TracedPath</code> from.
     */
    public TracedPath(Path root) {

        path = root.toAbsolutePath().toString();
    }

    /**
     * Creates a new instance of the <code>TracedPath</code> class.
     * @param root <code>TracedPath</code>: The root <code>TracedPath</code> to initiate this <code>TracedPath</code> from.
     * @param pathString <code>String...</code>: The path to extend along.
     */
    private TracedPath(TracedPath root, String... pathString) {

        this.path = Path.of(root.path, pathString).toString();
    }

    /**
     * Extends this <code>TracedPath</code> object along a directory branch.
     * @param pathString <code>String...</code>: The path to extend along.
     * @return <code>TracedPath</code>: The generated <code>TracedPath</code> instance.
     */
    public TracedPath extend(String... pathString) {

        return new TracedPath(this, pathString);
    }

    /**
     * Retrieve the path generated in this <code>TracedPath</code> instance.
     * @return <code>Path</code>: The generated <code>Path</code> object.
     */
    public Path getPath() {

        return Path.of(path);
    }

    /**
     * Returns the string representation of this <code>TracedPath</code> instance.
     * @return <code>String</code>: This <code>TracedPath</code> instance in the following string representation: <br>"<code>absolute/pathway/example.txt</code>"
     */
    @Override
    public String toString() {

        return getPath().toString();
    }
}
