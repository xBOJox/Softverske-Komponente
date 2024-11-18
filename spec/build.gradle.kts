plugins {
    kotlin("jvm")
    `java-library`
    id("org.jetbrains.dokka") version "1.8.10"
    `maven-publish`
}



group = "rs.raf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


tasks.javadoc {
    dependsOn(tasks.dokkaJavadoc)
    doLast {
        println("Javadoc task completed with Dokka output.")
    }
}

tasks.dokkaJavadoc {
    outputDirectory.set(file("build/dokka/javadoc")) // Set the output directory
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "rs.raf"
            artifactId = "spec"
            version = "1.0.0"
        }
    }
}
kotlin {
    jvmToolchain(21)
}