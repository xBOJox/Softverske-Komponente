plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}


group = "rs.raf"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainClass.set("TestKt")
}


tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles() // include meta-inf services files
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

//tasks.jar {
//    dependsOn(configurations.runtimeClasspath.get().map {
//        if (it.isDirectory)
//            it
//        else
//            zipTree(it)
//    })
//    manifest.attributes["Main-Class"] = "TestKt"
//
//    from(configurations.runtimeClasspath.get().map {
//        if (it.isDirectory)
//            it
//        else
//            zipTree(it)
//    })
//    duplicatesStrategy = DuplicatesStrategy.INCLUDE
//}

tasks.register<Sync>("copyResources") {
    from(configurations.runtimeClasspath.get())
    into(layout.buildDirectory.dir("libs"))
}




dependencies {
    implementation(project(":spec"))
    implementation(project(":excelImpl"))
    implementation(project(":csvImpl"))
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}


