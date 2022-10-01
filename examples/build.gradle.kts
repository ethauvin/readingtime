import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.42.0"
    kotlin("jvm") version "1.7.20"
}

// ./gradlew run
// ./gradlew runJava

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.example.ReadingTimeExampleKt")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

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
