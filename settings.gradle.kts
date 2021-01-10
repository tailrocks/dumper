rootProject.name = "dumper"

include(
        // libraries
        ":dumper-data",
        ":dumper-data-repositories",

        // apps
        ":dumper-api",
        ":dumper-cli",
        ":dumper-share",
        ":dumper-test"
)
