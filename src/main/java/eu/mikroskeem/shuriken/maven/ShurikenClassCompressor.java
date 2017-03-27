package eu.mikroskeem.shuriken.maven;

import eu.mikroskeem.shuriken.common.SneakyThrow;
import eu.mikroskeem.shuriken.common.streams.ByteArrays;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Mark Vainomaa
 */
@Mojo(name = "compressed-package", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class ShurikenClassCompressor extends AbstractMojo {

    @Parameter(defaultValue = "target/classes", readonly = true, required = true)
    private String classesDirectory = "target/classes";

    @Override
    @SneakyThrows(IOException.class)
    public void execute() throws MojoExecutionException, MojoFailureException {
        BrotliLibraryLoader.loadBrotli();

        getLog().info("Processing compiled class files...");
        Files.walk(Paths.get(classesDirectory))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".class"))
                .forEach(p -> {
                    Path target = Paths.get(p.toString() + ".br");
                    try {
                        BrotliStreamCompressor compressor = new BrotliStreamCompressor();
                        byte[] content = compressor.compressArray(ByteArrays.fromInputStream(Files.newInputStream(p)), true);
                        try (OutputStream os = Files.newOutputStream(target)) {
                            os.write(content);
                            os.flush();
                        }
                        getLog().info(String.format("Wrote compressed class to: %s", target.toString()));
                    } catch (Throwable e){
                        SneakyThrow.throwException(e);
                    }
        });
    }
}
