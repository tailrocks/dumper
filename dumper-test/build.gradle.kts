plugins {
    `java-library`
}

dependencies {
    // subprojects
    implementation(project(":dumper-share"))

    // Jambalaya
    implementation("io.github.expatiat.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")

    // libraries
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
}
