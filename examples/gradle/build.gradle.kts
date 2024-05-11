import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("application")
    id("com.github.ben-manes.versions") version "0.51.0"
    kotlin("jvm") version "1.9.24"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.3-SNAPSHOT")
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

    register<JavaExec>("runJava") {
        group = "application"
        mainClass.set("com.example.ReadingTimeSample")
        classpath = sourceSets.main.get().runtimeClasspath
    }
}
