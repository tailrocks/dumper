plugins {
    id("io.micronaut.library")
}

micronaut {
    version(Versions.micronaut)
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.zhokhov.*")
    }
}

dependencies {
    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")

    // Jackson
    // TODO version
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.0")
}
