plugins {
    id("com.gorylenko.gradle-git-properties")
    id("com.adarshr.test-logger")
    id("io.micronaut.application")
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
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
    implementation(project(":dumper-data-repositories"))
    implementation(project(":dumper-schema"))
    testImplementation(project(":dumper-graphql-client"))

    // Micronaut
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    kapt(enforcedPlatform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut.data:micronaut-data-processor")
    kaptTest(enforcedPlatform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    kaptTest("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.graphql:micronaut-graphql")
    implementation("io.micronaut.security:micronaut-security-jwt")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("io.micronaut.kotlin:micronaut-kotlin-runtime")

    // Kotlin
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // GraphQL
    implementation("com.graphql-java-kickstart:graphql-java-tools:${Versions.graphQlTools}")
    implementation("com.zhokhov.graphql:graphql-java-datetime:${Versions.graphQlDateTime}")

    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-graphql:${Versions.jambalayaGraphql}")
    implementation("com.zhokhov.jambalaya:jambalaya-graphql-jooq:${Versions.jambalayaGraphqlJooq}")
    implementation("com.zhokhov.jambalaya:jambalaya-micronaut-graphql:${Versions.jambalayaMicronautGraphql}")
    testImplementation("com.zhokhov.jambalaya:jambalaya-graphql-apollo:${Versions.jambalayaGraphqlApollo}")
    testImplementation("com.zhokhov.jambalaya:jambalaya-kotlin-test:${Versions.jambalayaKotlinTest}")

    // libraries
    implementation("at.favre.lib:bcrypt:0.9.0") // TODO
    implementation("com.google.crypto.tink:tink:1.5.0") // TODO
}

gitProperties {
    keys = listOf("git.branch", "git.commit.id", "git.commit.id.abbrev", "git.commit.time")
    dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
    dateFormatTimeZone = "UTC"
}

application {
    mainClass.set("com.zhokhov.dumper.api.ApiApplication")
}

tasks {
    dockerBuild {
        val dockerRepository = System.getenv("DOCKER_REPOSITORY") ?: project.name
        images.set(setOf("$dockerRepository:${project.version}", "$dockerRepository:latest"))
    }
}
