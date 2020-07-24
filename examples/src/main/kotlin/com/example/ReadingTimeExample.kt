package com.example

import java.io.File
import net.thauvin.erik.readingtime.ReadingTime
import net.thauvin.erik.readingtime.ReadingTime.Companion.imgCount
import net.thauvin.erik.readingtime.ReadingTime.Companion.wordCount

fun main(args: Array<String>) {
    if (args.size == 1) {
        val text = File(args[0])
        if (text.exists() && text.canRead()) {
            val rt = ReadingTime(text.readText())
            rt.postfix = "minute to read"
            rt.plural = "minutes to read"

            println("It will take ${rt.calcReadingTime()} ${wordCount(rt.text)} words and ${imgCount(rt.text)} images.")
        } else {
            System.err.println("The file could not be read or found.")
        }
    } else {
        System.err.println("Please specify a file as an argument.")
    }
}
