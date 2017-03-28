package eu.mikroskeem.shuriken.maven;

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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

/**
 * @author Mark Vainomaa
 */
@Mojo(name = "shuriken", defaultPhase = LifecyclePhase.PACKAGE)
public class ShurikenClassCompressor extends AbstractMojo {

    @Parameter(defaultValue = "target/${project.build.finalName}.jar")
    private String sourceJarName;

    @Parameter(defaultValue = "target/${project.build.finalName}-compressed.jar")
    private String targetJarName;

    @Parameter(defaultValue = "false")
    private boolean replaceMainJar;

    @Override
    @SneakyThrows(IOException.class)
    public void execute() throws MojoExecutionException, MojoFailureException {
        /* Load Brotli library */
        BrotliLibraryLoader.loadBrotli();
        File sourceJar = new File(sourceJarName);
        File targetJar = new File(targetJarName);

        getLog().info(String.format("Processing jar: %s", sourceJar.getAbsolutePath()));
        if(!sourceJar.exists())
            throw new MojoExecutionException("No source jar found!");
        if(!targetJar.exists())
            assert targetJar.createNewFile() : "Failed to create target jar!";

        getLog().info("Processing compiled class files...");
        try(
                FileSystem source = ZipFilesystem.newFs(sourceJar);
                FileSystem target = ZipFilesystem.newFs(targetJar)
        ) {
            Files.walk(source.getPath("/"), Integer.MAX_VALUE).forEach(s-> process(target, s));
        }
        if(replaceMainJar){
            Files.move(targetJar.toPath(), sourceJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            getLog().info(String.format(
                    "Processing done! Replaced main jar with processed jar: %s",
                    sourceJar.getAbsolutePath()
            ));
        } else {
            getLog().info(String.format("Processing done! Target jar: %s", targetJar.getAbsolutePath()));
        }
    }

    @SneakyThrows(IOException.class)
    private void process(FileSystem target, Path sourceFile){
        if(Files.isRegularFile(sourceFile)){
            Path targetFile = target.getPath(sourceFile.toString());
            if(!Files.exists(targetFile.getParent()))
                Files.createDirectories(targetFile.getParent());
            if(sourceFile.getFileName().toString().endsWith(".class")) {
                /* Process class file */
                Path targetClass = target.getPath(sourceFile.toString() + ".br");
                try (
                        BrotliStreamCompressor compressor = new BrotliStreamCompressor();
                        OutputStream targetOutput = Files.newOutputStream(targetClass)
                ) {
                    /* Compress */
                    byte[] content = compressor.compressArray(
                            ByteArrays.fromInputStream(Files.newInputStream(sourceFile)),
                            true
                    );

                    /* Write file */
                    targetOutput.write(content);
                    targetOutput.flush();
                    getLog().info(String.format("Wrote compressed class to: %s", targetClass.toString()));
                }
            } else {
                Files.copy(sourceFile, targetFile);
            }
        }
    }
}
