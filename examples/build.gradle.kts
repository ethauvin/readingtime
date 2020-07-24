plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.github.ben-manes.versions") version "0.28.0"
    application
}

// ./gradlew run
// ./gradlew runJava

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("net.thauvin.erik:readingtime:0.9.0")
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
