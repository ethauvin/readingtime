import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    id("org.jetbrains.dokka") version "1.4.30"
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
    id("org.sonarqube") version "3.1.1"
    `java-library`
    `maven-publish`
    jacoco
    signing
}

description = "Estimated Reading Time for Blog Posts, Articles, etc."
group = "net.thauvin.erik"
version = "0.9.0"

val deployDir = "deploy"
val gitHub = "ethauvin/$name"
val mavenUrl = "https://github.com/$gitHub"
val publicationName = "mavenJava"
var isRelease = "release" in gradle.startParameter.taskNames

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jsoup:jsoup:1.13.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

detekt {
    baseline = project.rootDir.resolve("config/detekt/baseline.xml")
}

sonarqube {
    properties {
        property("sonar.projectKey", "ethauvin_$name")
        property("sonar.sourceEncoding", "UTF-8")
    }
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
}

tasks {
    withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<GenerateMavenPom> {
        destination = file("$projectDir/pom.xml")
    }

    assemble {
        dependsOn(javadocJar)
    }

    clean {
        doLast {
            project.delete(fileTree(deployDir))
        }
    }

    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            named("main") {
                moduleName.set("ReadingTime")
                apiVersion.set("${project.version}")
            }
        }
    }

    val copyToDeploy by registering(Copy::class) {
        from(configurations.runtimeClasspath) {
            exclude("annotations-*.jar")
        }
        from(jar)
        into(deployDir)
    }

    val gitIsDirty by registering(Exec::class) {
        description = "Fails if git has uncommitted changes."
        group = "verification"
        commandLine("git", "diff", "--quiet", "--exit-code")
    }

    val gitTag by registering(Exec::class) {
        description = "Tags the local repository with version ${project.version}"
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn(gitIsDirty)
        if (isRelease) {
            commandLine("git", "tag", "-a", project.version, "-m", "Version ${project.version}")
        }
    }

    register("deploy") {
        description = "Copies all needed files to the $deployDir directory."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn("build", "jar")
        outputs.dir(deployDir)
        inputs.files(copyToDeploy)
        mustRunAfter("clean")
    }

    register("release") {
        description = "Publishes version ${project.version} to local repository."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn("wrapper", "deploy", "gitTag", "publishToMavenLocal")
    }

    "sonarqube" {
        dependsOn("jacocoTestReport")
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(mavenUrl)
                licenses {
                    license {
                        name.set("BSD 3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set("ethauvin")
                        name.set("Erik C. Thauvin")
                        email.set("erik@thauvin.net")
                        url.set("https://erik.thauvin.net/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$gitHub.git")
                    developerConnection.set("scm:git:git@github.com:$gitHub.git")
                    url.set("$mavenUrl")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("$mavenUrl/issues")
                }
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials(PasswordCredentials::class)
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications[publicationName])
    }
}
