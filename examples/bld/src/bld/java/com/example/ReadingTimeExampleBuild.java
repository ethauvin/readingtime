package com.example;

import rife.bld.BuildCommand;
import rife.bld.extension.CompileKotlinOperation;
import rife.bld.operations.RunOperation;
import rife.bld.BaseProject;

import java.util.List;


import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;

public class ReadingTimeExampleBuild extends BaseProject {
    public ReadingTimeExampleBuild() {
        pkg = "com.example";
        name = "ReadingTimeExample";
        version = version(0, 1, 0);

        mainClass = "com.example.ReadingTimeExampleKt";

        javaRelease = 11;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL, CENTRAL_SNAPSHOTS);

        scope(compile)
                .include(dependency("net.thauvin.erik", "readingtime", version(0, 9, 3, "SNAPSHOT")));
    }

    public static void main(String[] args) {
        new ReadingTimeExampleBuild().start(args);
    }

    @Override
    public void compile() throws Exception {
        new CompileKotlinOperation()
                .fromProject(this)
                .execute();

        // Also compile the Java source code
        super.compile();
    }

    @BuildCommand(value = "run-java", summary = "Runs the Java example")
    public void runJava() throws Exception {
        new RunOperation()
                .fromProject(this)
                .mainClass("com.example.ReadingTimeSample")
                .execute();
    }
}
