// The build script for the convention plugins in buildSrc. Convention plugins are build scripts in 'src/main' that
// automatically become available as plugins in the main build.

plugins {
    `kotlin-dsl` // Support writing convention plugins in Kotlin.
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // Makes the plugin org.jetbrains.kotlin.jvm available via the library org.jetbrains.kotlin:kotlin-gradle-plugin,
    // whose version is declared in the version catalog (gradle/libs.versions.toml).
    implementation(libs.kotlin.gradle.plugin)
}
