# Shuriken Maven Plugin

Compress Java class files with [Brotli](https://en.wikipedia.org/wiki/Brotli), to reduce size drastically.

Brotli was chosen, because it compressed class files 
[about 2x smaller](https://git.wut.ee/mikroskeem/java-class-compression-research) (depending on class file size)

## Notes
Custom classloader is required. That's under development right now.

## POM snippet:

```
<plugin>
    <groupId>eu.mikroskeem</groupId>
    <artifactId>shuriken.mavenplugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <configuration>
        <sourceJar>target/${project.build.finalName}.jar</sourceJar>             <!-- Source jar -->
        <targetJar>target/${project.build.finalName}-compressed.jar</targetJar>  <!-- Target jar -->
        <replaceMainJar>false</replaceMainJar>                                   <!-- To replace source jar -->
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>                                               <!-- Bind to package goal -->
            <goals>
                <goal>shuriken</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```