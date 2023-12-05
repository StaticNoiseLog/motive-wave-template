MotiveWave Gradle Project Template
==================================

This Gradle project can be used as a starting point when writing custom studies (indicators) and strategies
for [MotiveWave](https://www.motivewave.com/). It has been tested
with [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) but does not contain dependencies on a specific IDE. Any
IDE with good support for Gradle should work.

Usage
-----

The idea is that you create a copy of this project that you can modify to fit your needs. For instance, you might want
to change the package names from `com.examples.mw` to something more suitable.

### Kotlin and Java

The project is set up for Kotlin. But you can use Java if you prefer, and you can even mix both languages. Just make
sure that all Kotlin code goes in the `src/main/kotlin` source set and all Java code in `src/main/java`. At least this
is what is stated on kotlinlang.org at the time of writing (2023):  
[Do not store Java .java files in the src/*/kotlin directory, as the .java files will not be compiled. Instead, you can use src/main/java.](https://kotlinlang.org/docs/gradle-configure-project.html#kotlin-and-java-sources)

### Deployment

The Gradle "build" task automatically copies the resulting JAR to the `MotiveWave Extensions` directory and "touches"
the `.last_updated` file, which should trigger a reload of the custom indicators by MotiveWave.

By default, the name of the library with the custom indicators and studies is composed of the current username
with `_mw.jar` appended, e.g. `bill_mw.jar`. If you are happy with this default, things should work out of the box on
Windows, Linux and macOS. Otherwise, see the first two lines of [this build script](./lib/build.gradle.kts) for
customization of the JAR file name and MotiveWave directory location.

### Development Cycle

With MotiveWave running, a developer's workflow should be as straightforward as initiating a Gradle "build" task in the
IDE or on the command line. Once the task is completed, any new or updated objects should automatically appear in
MotiveWave's "Study" and "Strategy" menu.

Project Organization and Dependencies
-------------------------------------

This Gradle project uses various modern technologies (as of 2023) to organize the build process and source code.

### JPMS

The JARs produced by this Gradle build are modularized with the JPMS. JPMS stands for "Java Platform Module System".
Previously known as Project Jigsaw and introduced with Java 9, it facilitates proper modularization.

The two JPMS modules in this project are [motive.wave.custom.lib](./lib/src/main/java/module-info.java)
and [motive.wave.custom.common](./common/src/main/java/module-info.java).

The "motive.wave.custom.lib" module has a dependency on the MotiveWave SDK and serves as the space for developing
specific studies and strategies. The 'motive.wave.custom.common' module is designed for code that does not rely on the
MotiveWave SDK and can be reused across various studies or strategies. If necessary, additional modules can be added
by essentially duplicating the "common" module.

When utilizing JPMS with Gradle, you must employ Gradle subprojects. In this context, a JPMS module corresponds to a
Gradle subproject. Despite the 1:1 correlation, these two constructs serve different purposes. JPMS modules group
packages and provide metadata about the whole unit, including its dependencies and public interfaces. These constraints
are then enforced by the compiler and at runtime (true modularization). On the other hand, Gradle's multi-project
feature (subprojects) is a part of the build system and primarily aims at structuring the build. For practical purposes
you do not have to worry about these theoretical differences. Just realize that the Gradle subprojects [lib](./lib)
and [common](./common) directly correspond to the JPMS
modules [motive.wave.custom.lib](./lib/src/main/java/module-info.java)
and [motive.wave.custom.common](./common/src/main/java/module-info.java).

### MotiveWave SDK

The [JAR with the MotiveWave SDK](./lib/local-jars) was taken
from [MotiveWave_Studies.zip](https://www.motivewave.com/support/sdk.htm). Since this JAR file is not yet a JPMS module,
the [Extra Java Module Info Gradle plugin](https://github.com/gradlex-org/extra-java-module-info/tree/main) is employed
to
add module information to it, enabling its use in this modular Java project.

### Gradle Version Catalog

The [version catalog](gradle/libs.versions.toml) is a lesser known feature introduced with Gradle 7.0. It allows the
central management of dependency versions. This is the place to check if you want to learn about or modify the versions
of dependencies used in any of the subprojects.

Note that the version catalog is irrelevant for the MotiveWave SDK because the SDK is not obtained as a dependency from
a repository but rather included as a JAR file.

### Gradle Convention Plugins (buildSrc)

Gradle's convention plugins are a powerful tool for reusing build configurations. They enable you to define a set of
conventions centrally and then apply those conventions to different subprojects. This simplifies build management and
enhances efficiency.

The code for the single convention plugin used by this project can be found under [Gradle's buildSrc](./buildSrc). The
structure of this Gradle project is so simple that using a convention plugin is a bit overkill. The reason for
introducing it was to get rid of the following unexpected build warning produced by Gradle 8.5:  
*The Kotlin Gradle plugin was loaded multiple times in different subprojects, which is not supported and may break the
build.*

### Gradle JVM Toolchain

A Java toolchain is a collection of tools used to build and run Java projects. It is typically provided by the
environment through local JRE or JDK installations. Beginning with version 6.7, Gradle introduced the JVM Toolchains
feature, enabling the specification of the desired JDK version for a build and prompting Gradle to download the
necessary JDK if it cannot be located in the build environment.

The toolchain is configured in
the [convention plugin](./buildSrc/src/main/kotlin/motive-wave-template.kotlin-shared-conventions.gradle.kts) used by
all subprojects.

### Versioning

The Gradle plugin [ch.fuzzle.gradle.semver](https://github.com/f-u-z-z-l-e/semver-plugin) is used for versioning this
project with Git tags. If you have no need for versioning or want to do it differently, you can simly remove all
references to "fuzzle".