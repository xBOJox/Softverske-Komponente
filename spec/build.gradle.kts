plugins {
    kotlin("jvm")
    `java-library`
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
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "raf.rs"
            artifactId = "spec"
            version = "1.0.0"
        }
    }
}
kotlin {
    jvmToolchain(21)
}