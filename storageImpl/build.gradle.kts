plugins {
    kotlin("jvm")
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
            artifactId = "storageImpl"
            version = "1.0.0"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("rs.raf:storageSpec:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}