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
        annotations("com.zhokhov.dumper.*")
    }
}

dependencies {
    // subprojects
    api(project(":libs:dumper-data"))

    // Micronaut
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    api("io.micronaut.data:micronaut-data-model")

    // Jambalaya
    api("com.zhokhov.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")
    api("com.zhokhov.jambalaya:jambalaya-checks-jooq:${Versions.jambalayaChecksJooq}")

    // libraries
    // TODO https://github.com/micronaut-projects/micronaut-starter/issues/497 (Micronaut 2.3.x)
    api("javax.annotation:javax.annotation-api")
}
