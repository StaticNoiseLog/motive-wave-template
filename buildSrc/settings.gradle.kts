dependencyResolutionManagement {
    // Make the version catalog (gradle/libs.versions.toml) from the main build available in buildSrc.
    versionCatalogs {
        create("libs", { from(files("../gradle/libs.versions.toml")) })
    }
}

rootProject.name = "buildSrc"