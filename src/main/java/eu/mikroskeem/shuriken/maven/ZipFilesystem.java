package eu.mikroskeem.shuriken.maven;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mark Vainomaa
 */
class ZipFilesystem {
    static FileSystem newFs(File zipFile) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI uri = URI.create("jar:file:" + zipFile.getAbsolutePath());
        return FileSystems.newFileSystem(uri, env);
    }
}
