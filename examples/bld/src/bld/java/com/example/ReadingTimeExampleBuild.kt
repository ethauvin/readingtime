package com.example

import rife.bld.BaseProject
import rife.bld.BuildCommand
import rife.bld.dependencies.Repository.*
import rife.bld.dependencies.Scope.compile
import rife.bld.extension.CompileKotlinOperation
import rife.bld.operations.RunOperation

class ReadingTimeExampleBuild : BaseProject() {
    init {
        pkg = "com.example"
        name = "ReadingTimeExample"
        version = version(0, 1, 0)

        mainClass = "com.example.ReadingTimeExampleKt"

        javaRelease = 17
        downloadSources = true
        autoDownloadPurge = true
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL, CENTRAL_SNAPSHOTS)

        scope(compile)
                .include(dependency("net.thauvin.erik", "readingtime", version(0, 9, 3, "SNAPSHOT")));
    }

    public static void main(String[] args) {
        new ReadingTimeExampleBuild().start(args);
    }

    @Override
    @kotlin.Throws(Exception::class)
    fun compile() {
        CompileKotlinOperation()
            .fromProject(this)
            .execute()

        // Also compile the Java source code
        super.compile()
    }

    @BuildCommand(value = "run-java", summary = "Runs the Java example")
    @kotlin.Throws(Exception::class)
    fun runJava() {
        RunOperation()
            .fromProject(this)
            .mainClass("com.example.ReadingTimeSample")
            .execute()
    }

    companion object {
        fun main(args: Array<String?>?) {
            com.example.ReadingTimeExampleBuild().start(args)
        }
    }
}
