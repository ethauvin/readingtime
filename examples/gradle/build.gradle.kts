import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.53.0"
    kotlin("jvm") version "2.2.21"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.3-SNAPSHOT")
}

application {
    mainClass.set("com.example.ReadingTimeExampleKt")
}

tasks {
    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.ReadingTimeSample")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
