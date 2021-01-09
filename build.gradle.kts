import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    id("com.jfrog.bintray") version Versions.gradleBintrayPlugin apply false
    id("com.diffplug.spotless") version Versions.gradleSpotlessPlugin
    id("com.gorylenko.gradle-git-properties") version Versions.gradleGitPropertiesPlugin apply false
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin apply false
    id("io.micronaut.application") version Versions.gradleMicronautPlugin apply false
    id("io.micronaut.library") version Versions.gradleMicronautPlugin apply false
    kotlin("jvm") version Versions.kotlin apply false
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

    idea {
        module {
            isDownloadJavadoc = false
            isDownloadSources = false
        }
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/expatiat/jambalaya") }
    }

    spotless {
        java {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
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
        apply(plugin = "com.jfrog.bintray")
    }

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

        configure<BintrayExtension> {
            user = System.getenv("BINTRAY_USER")
            key = System.getenv("BINTRAY_KEY")
            publish = true
            override = true
            setPublications("mavenJava")
            pkg.apply {
                repo = "dumper"
                name = "dumper"
                userOrg = "expatiat"
                vcsUrl = "https://github.com/expatiat/dumper.git"
                setLicenses("Apache-2.0")
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
    if (gradle.startParameter.isConfigureOnDemand
            && buildscript.sourceFile?.extension?.toLowerCase() == "kts"
            && parent != rootProject) {
        generateSequence(parent) { project -> project.parent.takeIf { it != rootProject } }
                .forEach { evaluationDependsOn(it.path) }
    }
}
