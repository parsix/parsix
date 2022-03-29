import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.parsix"
version = "0.1.0"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.dokka") version "1.4.32"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // needed for publication on maven central
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

repositories {
    mavenCentral()
}

sourceSets {
    create("samples") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    languageVersion = "1.5"
}

val dokkaDir = buildDir.resolve("docs")
tasks.dokkaHtml.configure {
    outputDirectory.set(dokkaDir)
    dokkaSourceSets {
        configureEach {
            samples.from("test/kotlin", "samples/kotlin")
        }
    }
}

val cleanUpDokkaTask by tasks.register<Delete>("cleanUpDokka") {
    delete(dokkaDir)
}

val docsJar = tasks.register<Jar>("docJar") {
    dependsOn(cleanUpDokkaTask, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaDir)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("main") {
            from(components["java"])
            artifact(docsJar)
            artifact(sourcesJar)

            pom {
                name.set("Parsix")
                description.set("High level input and data parser")
                url.set("https://github.com/parsix/parsix")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("Salvatore Pelligra")
                        email.set("pelligra.s@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/parsix/parsix.git")
                    developerConnection.set("scm:git:ssh://github.com:parsix/parsix.git")
                    url.set("https://github.com/parsix/parsix")
                }

                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/parsix/parsix/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["main"])
}