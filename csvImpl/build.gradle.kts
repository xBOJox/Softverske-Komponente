plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "rs.raf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("rs.raf:spec:1.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "rs.raf"
            artifactId = "csvImpl"
            version = "1.0.0"
        }
    }
}
kotlin {
    jvmToolchain(21)
}