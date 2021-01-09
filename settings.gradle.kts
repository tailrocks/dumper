rootProject.name = "dumper"

include(
        // libraries
        ":libs:dumper-data",
        ":libs:dumper-data-repositories",

        // apps
        ":dumper-api",
        ":dumper-cli",
        ":dumper-share",
        ":dumper-test"
)
