// The build script for the convention plugins in buildSrc. Convention plugins are build scripts in 'src/main' that
// automatically become available as plugins in the main build.

plugins {
    `kotlin-dsl` // Support writing convention plugins in Kotlin.
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // Make the Gradle JVM plugin for Kotlin (org.jetbrains.kotlin.jvm) available by adding a dependency on the
    // library org.jetbrains.kotlin:kotlin-gradle-plugin through the version catalog (gradle/libs.versions.toml).
    // The version of the plugin is declared in the version catalog.
    implementation(libs.kotlin.gradle.plugin)
}