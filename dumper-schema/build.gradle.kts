plugins {
    `java-library`
}

dependencies {
    // Jambalaya
    implementation("io.github.expatiat.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")

    // SpotBugs
    implementation("com.github.spotbugs:spotbugs-annotations:${Versions.spotbugsAnnotations}")
}
