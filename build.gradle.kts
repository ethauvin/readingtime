import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.github.ben-manes.versions") version "0.31.0"
    id("com.jfrog.bintray") version "1.8.5"
    id("io.gitlab.arturbosch.detekt") version "1.12.0"
    id("org.jetbrains.dokka") version "1.4.0"
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("org.sonarqube") version "3.0"
    `java-library`
    `maven-publish`
    jacoco
}

description = "Estimated Reading Time for Blog Posts, Articles, etc."
group = "net.thauvin.erik"
version = "0.9.0"

val deployDir = "deploy"
val gitHub = "ethauvin/$name"
val mavenUrl = "https://github.com/$gitHub"
val publicationName = "mavenJava"
var isRelease = "release" in gradle.startParameter.taskNames

// Load local.properties
File("local.properties").apply {
    if (exists()) {
        FileInputStream(this).use { fis ->
            Properties().apply {
                load(fis)
                forEach { (k, v) ->
                    extra[k as String] = v
                }
            }
        }
    }
}
repositories {
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
}

detekt {
    baseline = project.rootDir.resolve("config/detekt/baseline.xml")
}

jacoco {
    toolVersion = "0.8.5"
}

sonarqube {
    properties {
        property("sonar.projectKey", "ethauvin_$name")
        property("sonar.sourceEncoding", "UTF-8")
    }
}


val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    description = "Assembles a JAR of the generated Javadoc."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
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
        dependsOn(sourcesJar, javadocJar)
    }

    clean {
        doLast {
            project.delete(fileTree(deployDir))
        }
    }

    dokkaJavadoc {
        dokkaSourceSets {
            configureEach {
                moduleDisplayName.set("ReadingTime")
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

    val bintrayUpload by existing(BintrayUploadTask::class) {
        dependsOn(publishToMavenLocal, gitTag)
        doFirst {
            versionName = "${project.version}"
            versionDesc = "${project.name} ${project.version}"
            versionVcsTag = "${project.version}"
            versionReleased = Date().toString()
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
        description = "Publishes version ${project.version} to Bintray."
        group = PublishingPlugin.PUBLISH_TASK_GROUP
        dependsOn("wrapper", bintrayUpload)
    }

    "sonarqube" {
        dependsOn("jacocoTestReport")
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintray.user")
    key = findProperty("bintray.apikey")
    publish = isRelease
    setPublications(publicationName)
    pkg.apply {
        repo = "maven"
        name = project.name
        desc = description
        websiteUrl = mavenUrl
        issueTrackerUrl = "$mavenUrl/issues"
        githubRepo = gitHub
        githubReleaseNotesFile = "README.md"
        vcsUrl = "$mavenUrl.git"
        setLabels(
            "articles",
            "blog",
            "java",
            "kotlin",
            "medium",
            "min",
            "posts",
            "read",
            "readingtime",
            "readtime",
            "weblog"
        )
        setLicenses("BSD 3-Clause")
        publicDownloadNumbers = true
        version.apply {
            name = project.version as String
            desc = description
            vcsTag = project.version as String
            gpg.apply {
                sign = true
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
            pom.withXml {
                asNode().apply {
                    appendNode("name", project.name)
                    appendNode("description", project.description)
                    appendNode("url", mavenUrl)

                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", "BSD 3-Clause")
                        appendNode("url", "https://opensource.org/licenses/BSD-3-Clause")
                    }

                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", "ethauvin")
                        appendNode("name", "Erik C. Thauvin")
                        appendNode("email", "erik@thauvin.net")
                    }

                    appendNode("scm").apply {
                        appendNode("connection", "scm:git:$mavenUrl.git")
                        appendNode("developerConnection", "scm:git:git@github.com:$gitHub.git")
                        appendNode("url", mavenUrl)
                    }

                    appendNode("issueManagement").apply {
                        appendNode("system", "GitHub")
                        appendNode("url", "$mavenUrl/issues")
                    }
                }
            }
        }
    }
}
