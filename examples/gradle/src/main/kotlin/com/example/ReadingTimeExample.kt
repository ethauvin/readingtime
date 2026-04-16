package com.example

import net.thauvin.erik.readingtime.ReadingTime
import java.io.File

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        with(File(args[0])) {
            if (exists() && canRead()) {
                val rt = ReadingTime(readText())
                rt.suffix = "minute to read"
                rt.pluralSuffix = "minutes to read"

                println(
                    "It will take ${rt.calcReadingTime()} ${rt.wordCount()} words and ${rt.imgCount()} images."
                )
            } else {
                System.err.println("The file could not be read or found.")
            }
        }
    } else {
        System.err.println("Please specify a file as an argument.")
    }
}
