plugins {
    id("io.micronaut.library")
}

micronaut {
    version(Versions.micronaut)
    testRuntime("junit5")
    processing {
        incremental(true)
        module(project.name)
        group(project.group.toString())
        annotations("com.zhokhov.*")
    }
}

dependencies {
    // subprojects
    api(project(":libs:dumper-data"))

    // Micronaut
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    api("io.micronaut:micronaut-inject")
    api("io.micronaut.data:micronaut-data-tx")
    api("io.micronaut.data:micronaut-data-model")

    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")
    implementation("com.zhokhov.jambalaya:jambalaya-checks-jooq:${Versions.jambalayaChecksJooq}")

    // libraries
    implementation("javax.annotation:javax.annotation-api")
}
