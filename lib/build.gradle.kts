val libraryName = System.getProperty("user.name").lowercase() + "_mw.jar"
val pathMotiveExtensions = System.getProperty("user.home") + File.separator + "MotiveWave Extensions"

plugins {
    id("motive-wave-template.kotlin-shared-conventions") // convention plugin from buildSrc
    `java-library`
    alias(libs.plugins.extra.java.module.info) // Gradle plugin
    alias(libs.plugins.ch.fuzzle.gradle.semver) // Git versioning plugin
}

dependencies {
    implementation(fileTree("local-jars") { include("*.jar") })
    implementation(project(":common"))
    testImplementation(libs.kotlin.test.junit5) // JUnit 5 integration for Kotlin
    testRuntimeOnly(libs.junit.jupiter.engine) // JUnit 5 engine for running tests
    testRuntimeOnly(libs.junit.platform.launcher) // JUnit 5 support for IDEs and build tools
}

extraJavaModuleInfo {
    module("mwave_sdk.jar", "mwave.sdk") {
        failOnMissingModuleInfo.set(false) // avoid error "Not a module and no mapping defined: annotations-13.0.jar"
        exports("com.motivewave.platform.sdk.common")
        exports("com.motivewave.platform.sdk.common.desc")
        exports("com.motivewave.platform.sdk.common.menu")
        exports("com.motivewave.platform.sdk.draw")
        exports("com.motivewave.platform.sdk.order_mgmt")
        exports("com.motivewave.platform.sdk.study")
        requiresTransitive("java.desktop") // avoid errors like "class file for java.awt.Color not found"
    }
}

// Add Kotlin runtime to our JAR so MotiveWave can run Kotlin code.
tasks.jar {
    dependsOn(configurations.runtimeClasspath) // to be sure that runtimeClasspath is complete because we get() from it
    rootProject.extra["archiveFile"] = archiveFile.get()
    from({
        configurations.runtimeClasspath.get().filter { it.name.startsWith("kotlin-stdlib") }.map {
            zipTree(it)
        }
    })
}

tasks {
    "build" {
        doLast {
            logger.lifecycle("Publishing library lib-${version}.jar as ${pathMotiveExtensions}${File.separator}$libraryName")
            copy {
                from(rootProject.extra["archiveFile"])
                into(pathMotiveExtensions)
                rename("lib-${version}.jar", libraryName)
            }
            // trigger reloading of library in MotiveWave by doing a "touch" on the .last_updated file
            val filePath = file("${pathMotiveExtensions}${File.separator}.last_updated")
            filePath.setLastModified(System.currentTimeMillis())
        }
    }
}

// Workaround required so that Kotlin code can be used in Java files. Note that the module name
// for the --patch-module argument must be the same as the module name in the module-info.java file.
tasks.compileJava {
    options.compilerArgumentProviders.add(object : CommandLineArgumentProvider {
        @CompileClasspath
        val kotlinClasses = kotlin.sourceSets.main.flatMap { it.kotlin.classesDirectory }

        override fun asArguments() = listOf(
            "--patch-module",
            "motive.wave.custom.lib=${kotlinClasses.get().asFile.absolutePath}" // module name must match module-info.java
        )
    })
}