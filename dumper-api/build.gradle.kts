plugins {
    id("com.gorylenko.gradle-git-properties")
    id("com.adarshr.test-logger")
    id("io.micronaut.application")
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
    implementation(project(":libs:dumper-data-repositories"))

    // Micronaut
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.graphql:micronaut-graphql")

    // GraphQL
    implementation("com.graphql-java-kickstart:graphql-java-tools:${Versions.graphQlTools}")
    implementation("com.zhokhov.graphql:graphql-java-datetime:${Versions.graphQlDateTime}")

    // Jambalaya
    implementation("com.zhokhov.jambalaya:jambalaya-graphql:${Versions.jambalayaGraphql}")
    implementation("com.zhokhov.jambalaya:jambalaya-graphql-jooq:${Versions.jambalayaGraphqlJooq}")
    implementation("com.zhokhov.jambalaya:jambalaya-micronaut-graphql:${Versions.jambalayaMicronautGraphql}")
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
        images.set(setOf("${dockerRepository}:${project.version}", "${dockerRepository}:latest"))
    }
}
