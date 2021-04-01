pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "dumper"

include(
    // libraries
    ":dumper-api-interface",
    ":dumper-data",
    ":dumper-data-repositories",
    ":dumper-graphql-client",
    ":dumper-schema",

    // apps
    ":dumper-api",
    ":dumper-cli",
    ":dumper-share",
    ":dumper-test"
)
