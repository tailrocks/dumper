plugins {
    id("org.flywaydb.flyway") version Versions.flyway
    id("nu.studer.jooq") version Versions.gradleJooqPlugin
    id("io.micronaut.library")
}

buildscript {
    configurations["classpath"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion(Versions.jooq)
        }
    }
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
    // Micronaut
    api("io.micronaut:micronaut-inject")
    api("io.micronaut:micronaut-validation")
    api("io.micronaut:micronaut-runtime")
    api("io.micronaut.flyway:micronaut-flyway")
    api("io.micronaut.sql:micronaut-jooq")
    api("io.micronaut.sql:micronaut-jdbc-hikari")

    // jOOQ
    jooqGenerator("org.postgresql:postgresql:${Versions.postgresql}")

    // libraries
    api("org.postgresql:postgresql:${Versions.postgresql}")
}

val datasourceUsername = System.getenv("DATASOURCE_USERNAME") ?: "postgres"
val datasourcePassword = System.getenv("DATASOURCE_PASSWORD") ?: "root"
val datasourceUrl = System.getenv("DATASOURCE_URL") ?: "jdbc:postgresql://127.0.0.1:19001/dumper_dev"
val datasourceDriver = "org.postgresql.Driver"

flyway {
    driver = datasourceDriver
    url = datasourceUrl
    user = datasourceUsername
    password = datasourcePassword
    locations = arrayOf("classpath:db/migration")
}

jooq {
    version.set(Versions.jooq)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("dumper") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.INFO
                jdbc.apply {
                    driver = datasourceDriver
                    url = datasourceUrl
                    user = datasourceUsername
                    password = datasourcePassword
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = listOf(
                                "flyway_schema_history",
                                "pg_stat_statements"
                        ).joinToString("|")
                        recordVersionFields = "version"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.zhokhov.dumper.data.jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks {
    flywayMigrate {
        dependsOn("processResources")
    }
    "generateDumperJooq" {
        dependsOn("flywayMigrate")
    }
    processResources {
        finalizedBy("generateDumperJooq")
    }
    compileJava {
        dependsOn("generateDumperJooq")
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                    "${buildDir}/generated-src/jooq/dumper"
            )
        }
    }
}
