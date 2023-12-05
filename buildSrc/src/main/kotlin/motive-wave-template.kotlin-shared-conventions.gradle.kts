/*
 * This code in buildSrc forms a plugin that can be used anywhere in the Gradle build. You can use this plugin in the
 * `build.gradle.kts` scripts of the subprojects as follows:
 *
 *      plugins {
 *          id("motive-wave-template.kotlin-shared-conventions") // from buildSrc
 *      }
 *
 * Writing plugins in buildSrc is a way to centralize common settings for the various subprojects in a Gradle build.
 * The common settings required by all subprojects in this Gradle build are the Kotlin plugin, the JVM toolchain, etc.
 *
 * There are limitations to using the version catalog (gradle/libs.versions.toml) in buildSrc. It works for plugins,
 * but it gets tricky with dependencies, see <https://github.com/gradle/gradle/issues/15383>.
 * Therefore, dependencies are not centralized here but remain in the build scripts of the individual subprojects.
 */
plugins {
    // This plugin is used through the dependency "implementation(libs.kotlin.gradle.plugin)" in
    // buildSrc/build.gradle.kts (which in turn refers to the version catalog (gradle/libs.versions.toml).
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20)) // must correspond with MotiveWave Java version
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform() // Use JUnit Platform for unit tests.
}