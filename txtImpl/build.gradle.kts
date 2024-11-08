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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "rs.raf"
            artifactId = "txtImpl"
            version = "1.0.0"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("rs.raf:spec:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}