plugins {
    `java-library`
}

dependencies {
    // subprojects
    implementation(project(":dumper-share"))

    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")

    // libraries
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
}
