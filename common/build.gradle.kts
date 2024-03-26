plugins {
    id("motive-wave-template.kotlin-shared-conventions") // convention plugin from buildSrc
    `java-library`
}

dependencies {
    testImplementation(libs.kotlin.test.junit5) // JUnit 5 integration for Kotlin
    testRuntimeOnly(libs.junit.jupiter.engine) // JUnit 5 engine for running tests
    testRuntimeOnly(libs.junit.platform.launcher) // JUnit 5 support for IDEs and build tools
}

// Workaround required so that Kotlin code can be used in Java files. Note that the module name
// for the --patch-module argument must be the same as the module name in the module-info.java file.
tasks.compileJava {
    options.compilerArgumentProviders.add(object : CommandLineArgumentProvider {
        @CompileClasspath
        val kotlinClasses = kotlin.sourceSets.main.flatMap { it.kotlin.classesDirectory }

        override fun asArguments() = listOf(
            "--patch-module",
            "motive.wave.custom.common=${kotlinClasses.get().asFile.absolutePath}" // module name must match module-info.java
        )
    })
}