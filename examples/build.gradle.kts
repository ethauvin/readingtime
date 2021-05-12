plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    application
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
    getByName<JavaExec>("run") {
       args = listOf("${project.projectDir}/example.html")
    }

    register<JavaExec>("runJava") {
        group = "application"
        main = "com.example.ReadingTimeSample"
        classpath = sourceSets.main.get().runtimeClasspath
        args = listOf("${project.projectDir}/example.html")
    }
}
