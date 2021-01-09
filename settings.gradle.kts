rootProject.name = "dumper"

include(
        // libraries
        ":libs:dumper-data",

        // apps
        ":dumper-api",
        ":dumper-cli",
        ":dumper-share",
        ":dumper-test"
)
