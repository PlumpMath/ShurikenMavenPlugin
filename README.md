# Shuriken Maven Plugin

Compress Java class files with [Brotli](https://en.wikipedia.org/wiki/Brotli), to reduce size drastically.

Brotli was chosen, because it compressed class files 
[about 2x smaller](https://git.wut.ee/mikroskeem/java-class-compression-research) (depending on class file size)

## Notes
Custom classloader is required. I've made simple 
[(Shuriken) classloader implementation](https://github.com/mikroskeem/Shuriken/tree/master/classloader)
for this purpose

## POM snippet:
### Maven repository:

```xml
<repository>
    <id>mikroskeem-repo</id>
    <name>mikroskeem Maven Repository</name>
    <url>https://repo.wut.ee/repository/mikroskeem-repo/</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

### Maven plugin:
```xml
<plugin>
    <groupId>eu.mikroskeem</groupId>
    <artifactId>shuriken.mavenplugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <configuration>
        <sourceJarName>target/${project.build.finalName}.jar</sourceJarName>             <!-- Source jar -->
        <targetJarName>target/${project.build.finalName}-compressed.jar</targetJarName>  <!-- Target jar -->
        <replaceMainJar>false</replaceMainJar>                                           <!-- To replace source jar -->
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>                                                       <!-- Bind to package goal -->
            <goals>
                <goal>shuriken</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
