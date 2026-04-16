package com.example;

import net.thauvin.erik.readingtime.ReadingTime;
import net.thauvin.erik.readingtime.ReadingTimeEstimator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadingTimeSample {

    public static void main(String[] args) {
        if (args.length >= 1) {
            final Path text = Path.of(args[0]);
            try {
                var rt = ReadingTimeEstimator.create(Files.readString(text), "minute to read", "minutes to read");
                System.out.println("It will take " + rt.readingTime() + ' ' + rt.wordCount() + " words.");
            } catch (IOException e) {
                System.err.println("The file could not be read or found.");
            }
        } else {
            System.err.println("Please specify a file as an argument.");
        }
    }
}
