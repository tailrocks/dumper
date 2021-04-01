import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.BufferedReader

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
    }
}

plugins {
    java
    idea
    `maven-publish`
    id("com.adarshr.test-logger") version Versions.gradleTestLoggerPlugin apply false
    id("net.rdrei.android.buildtimetracker") version Versions.gradleBuildTimeTrackerPlugin
    id("com.diffplug.spotless") version Versions.gradleSpotlessPlugin
    id("com.gorylenko.gradle-git-properties") version Versions.gradleGitPropertiesPlugin apply false
    id("com.apollographql.apollo") version Versions.gradleApolloPlugin apply false
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin apply false
    id("io.micronaut.application") version Versions.gradleMicronautPlugin apply false
    id("io.micronaut.library") version Versions.gradleMicronautPlugin apply false
    kotlin("jvm") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false
    kotlin("plugin.allopen") version Versions.kotlin apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

buildtimetracker {
    reporters {
        register("summary") {
            options["ordered"] = "true"
            options["barstyle"] = "none"
            options["shortenTaskNames"] = "false"
        }
    }
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "net.rdrei.android.buildtimetracker")
    apply(plugin = "com.diffplug.spotless")

    apply(from = "${project.rootDir}/gradle/dependencyUpdates.gradle.kts")

    idea {
        module {
            isDownloadJavadoc = false
            isDownloadSources = false
        }
    }

    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
    }

    spotless {
        java {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            targetExclude("**/generated/**")
        }
        kotlin {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
        }
        kotlinGradle {
            ktlint()
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(11)
    }
}

// TODO
var gitVersion = "0.1.0"

if (gitVersion == "") {
    val process = Runtime.getRuntime().exec("git rev-parse --verify --short HEAD")
    val content = process.inputStream.bufferedReader().use(BufferedReader::readText)
    gitVersion = "git-${content.trim().strip()}"

    println("Git version: $gitVersion")
}

val publishingProjects = setOf(
    "dumper-share",
    "dumper-test"
)

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.adarshr.test-logger")
    if (publishingProjects.contains(project.name)) {
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")
    }

    version = gitVersion
    group = "com.zhokhov.dumper"

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        withJavadocJar()
        withSourcesJar()
    }

    dependencies {
        // SpotBugs
        implementation("com.github.spotbugs:spotbugs-annotations:${Versions.spotbugsAnnotations}")

        // JUnit
        testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    }

    if (publishingProjects.contains(project.name)) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    versionMapping {
                        allVariants {
                            fromResolutionResult()
                        }
                    }
                }
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
            excludeEngines = setOf("junit-vintage")
        }
    }

    // TODO remove after that issue will be fixed
    // Address https://github.com/gradle/gradle/issues/4823: Force parent project evaluation before sub-project evaluation for Kotlin build scripts
    /*
    if (gradle.startParameter.isConfigureOnDemand &&
            buildscript.sourceFile?.extension?.toLowerCase() == "kts" &&
            parent != rootProject) {
        generateSequence(parent) { project -> project.parent.takeIf { it != rootProject } }
                .forEach { evaluationDependsOn(it.path) }
    }
     */
}
