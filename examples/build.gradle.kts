plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.39.0"
    kotlin("jvm") version "1.5.10"
}

// ./gradlew run
// ./gradlew runJava

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.1-SNAPSHOT")
}

application {
    mainClass.set("com.example.ReadingTimeExampleKt")
}

tasks {
    named<JavaExec>("run") {
        args = listOf("${project.projectDir}/example.html")
    }

    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.ReadingTimeSample")
        classpath = sourceSets.main.get().runtimeClasspath
        args = listOf("${project.projectDir}/example.html")
    }
}
