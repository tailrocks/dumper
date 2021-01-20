rootProject.name = "dumper"

include(
        // libraries
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
