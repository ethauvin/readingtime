package com.example

import net.thauvin.erik.readingtime.ReadingTime
import net.thauvin.erik.readingtime.ReadingTime.Companion.imgCount
import net.thauvin.erik.readingtime.ReadingTime.Companion.wordCount
import java.io.File

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        with(File(args[0])) {
            if (exists() && canRead()) {
                val rt = ReadingTime(readText())
                rt.postfix = "minute to read"
                rt.plural = "minutes to read"

                println(
                    "It will take ${rt.calcReadingTime()} ${wordCount(rt.text)} words and ${imgCount(rt.text)} images."
                )
            } else {
                System.err.println("The file could not be read or found.")
            }
        }
    } else {
        System.err.println("Please specify a file as an argument.")
    }
}
