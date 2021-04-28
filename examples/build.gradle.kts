plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    application
}

// ./gradlew run --args="example.html"
// ./gradlew runJava --args="example.html"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("net.thauvin.erik:readingtime:0.9.1")
}

application {
    mainClassName = "com.example.ReadingTimeExampleKt"
}

tasks {
    getByName<JavaExec>("run") {
       args = listOf("${project.projectDir}/example.html")
    }

    register<JavaExec>("runJava") {
        group = "application"
        main = "com.example.ReadingTimeSample"
        classpath = sourceSets["main"].runtimeClasspath
        args = listOf("${project.projectDir}/example.html")
    }
}
