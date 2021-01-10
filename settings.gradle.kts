rootProject.name = "dumper"

include(
        // libraries
        ":dumper-data",
        ":dumper-data-repositories",
        ":dumper-graphql-client",

        // apps
        ":dumper-api",
        ":dumper-cli",
        ":dumper-share",
        ":dumper-test"
)
