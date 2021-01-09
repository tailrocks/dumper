plugins {
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
}

micronaut {
    version(Versions.micronaut)
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.zhokhov.dumper.*")
    }
}

dependencies {
    // subprojects
    implementation(project(":dumper-share"))

    // Micronaut
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.picocli:micronaut-picocli")

    // TODO remove me later, need for GraalVM only
    implementation("io.micronaut.sql:micronaut-jdbc")
    // @end TODO remove me later

    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")

    // GraalVM
    compileOnly("org.graalvm.nativeimage:svm")

    // Picocli
    annotationProcessor("info.picocli:picocli-codegen:${Versions.picocli}")
    implementation("info.picocli:picocli")

    // libraries
    implementation("org.postgresql:postgresql")
    runtimeOnly("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("com.zhokhov.dumper.cli.DumperCommand")
}
