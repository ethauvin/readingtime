package com.example;

import net.thauvin.erik.readingtime.ReadingTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadingTimeSample {
    public static void main(String[] args) {
        if (args.length >= 1) {
            final Path text = Path.of(args[0]);
            try {
                final ReadingTime rt = new ReadingTime(Files.readString(text));
                rt.setPostfix("minute to read");
                rt.setPlural("minutes to read");

//                final Config config =
//                        new Config.Builder(Files.readString(text))
//                                .postfix("minute to read")
//                                .plural("minutes to read")
//                                .build();
//                final ReadingTime rt = new ReadingTime(config);

                System.out.println("It will take " + rt.calcReadingTime() + ' ' + ReadingTime.wordCount(rt.getText())
                        + " words and " + ReadingTime.imgCount(rt.getText()) + " images at " + rt.getWpm()
                        + " words per minute.");
            } catch (IOException e) {
                System.err.println("The file could not be read or found.");
            }
        } else {
            System.err.println("Please specify a file as an argument.");
        }
    }
}
