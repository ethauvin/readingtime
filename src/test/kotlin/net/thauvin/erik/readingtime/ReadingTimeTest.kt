/*
 * ReadingTimeTest.kt
 *
 * Copyright (c) 2020, Erik C. Thauvin (erik@thauvin.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *   Neither the name of this project nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.thauvin.erik.readingtime

import java.io.File
import java.math.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingTimeTest {
    private val img = """<img src="#">"""
    private val rt = ReadingTime("This is a <b>test</b>.\nWith an image: $img")
    private val blogPost = File("src/test/resources/post.html").readText()
    private val mediumPost = File("src/test/resources/medium.html").readText()
    private val twoSeventyFive = "word ".repeat(275)

    private fun calcImgTime(imgCount: Int): Double {
        var time = 0.0
        var offset = 12

        repeat(imgCount) {
            time += offset
            if (offset != 3) {
                offset--
            }
        }
        return time
    }

    private fun calcReadingTime(text: String, wpm: Int): Double {
        return (ReadingTime.wordCount(text) / wpm) * 60.0
    }

    @Test
    fun testWordCount() {
        assertEquals(0, ReadingTime.wordCount(" "), "empty")
        assertEquals(3, ReadingTime.wordCount("one two three"), "one two three")
        assertEquals(2, ReadingTime.wordCount("    one    two    "), "one two")
        assertEquals(7, ReadingTime.wordCount(rt.text), "text")
        assertEquals(505, ReadingTime.wordCount(blogPost), "blogPost")
        assertEquals(391, ReadingTime.wordCount(mediumPost), "mediumPost")
        assertEquals(275, ReadingTime.wordCount(twoSeventyFive), "275")
        assertEquals(275, ReadingTime.wordCount("$twoSeventyFive $img"), "275 + image")
    }

    @Test
    fun testImgCount() {
        assertEquals(1, ReadingTime.imgCount(rt.text), "text")
        assertEquals(11, ReadingTime.imgCount(blogPost), "blogPost")
        assertEquals(3, ReadingTime.imgCount(mediumPost), "mediumPost")
        assertEquals(1, ReadingTime.imgCount("$twoSeventyFive $img"), "275 + image")
        assertEquals(2, ReadingTime.imgCount("$twoSeventyFive $img $img"), "275 + 2 images")
    }

    @Test
    fun testReadingTimeInSec() {
        assertEquals(calcReadingTime(rt.text, rt.wpm) + calcImgTime(1), rt.calcReadingTimeInSec(), "text + image")

        rt.text = "$img ${img.uppercase()}"
        assertEquals(calcImgTime(2), rt.calcReadingTimeInSec(), "2 images")
        rt.excludeImages = true
        assertEquals(0.0, rt.calcReadingTimeInSec(), "image uppercase")
        rt.excludeImages = false

        rt.text = blogPost
        assertEquals(
            calcReadingTime(rt.text, rt.wpm) + calcImgTime(11), rt.calcReadingTimeInSec(), "blogPost"
        )

        rt.excludeImages = true
        assertEquals(calcReadingTime(rt.text, rt.wpm), rt.calcReadingTimeInSec(), "exclude images")
        rt.extra = 60
        assertEquals(calcReadingTime(rt.text, rt.wpm) + 60L, rt.calcReadingTimeInSec(), "extra 60")
        rt.extra = 0
        rt.excludeImages = false

        rt.text = mediumPost
        rt.wpm = 300
        assertEquals(calcReadingTime(rt.text, 300) + calcImgTime(3), rt.calcReadingTimeInSec(), "mediumPost 300 wpm")
        rt.wpm = 275

        rt.text = "This is a test"
        assertEquals(0.0, rt.calcReadingTimeInSec(), "test")

        rt.text = twoSeventyFive
        assertEquals(60.0, rt.calcReadingTimeInSec(), "275")

        rt.text = "$twoSeventyFive $img"
        assertEquals(72.0, rt.calcReadingTimeInSec(), "275 + image")

        rt.text = "$twoSeventyFive $img $img"
        assertEquals(83.0, rt.calcReadingTimeInSec(), "275 + 2 images")

        rt.text = "$twoSeventyFive ${img.repeat(10)}"
        assertEquals(60.0 + 12 + 11 + 10 + 9 + 8 + 7 + 6 + 5 + 4 + 3, rt.calcReadingTimeInSec(), "10 images")

        rt.text = "$twoSeventyFive ${img.repeat(10)} $img"
        assertEquals(135.0 + 3, rt.calcReadingTimeInSec(), "11 images")

        rt.text = "$twoSeventyFive $twoSeventyFive"
        assertEquals(120.0, rt.calcReadingTimeInSec(), "275*2")

        rt.text = ""
        assertEquals(0.0, rt.calcReadingTimeInSec(), "empty")
    }

    @Test
    fun testReadingTime() {
        rt.text = blogPost
        assertEquals("2 min read", rt.calcReadingTime(), "blogPost")

        rt.plural = "mins read"
        assertEquals("2 mins read", rt.calcReadingTime(), "plural")

        rt.text = mediumPost
        rt.plural = ""
        assertEquals("2", rt.calcReadingTime(), "mediumPost")

        rt.text = "This is a test."
        rt.postfix = ""
        assertEquals("0", rt.calcReadingTime(), "test")

        rt.text = ""
        assertEquals("0", rt.calcReadingTime(), "empty")

        rt.text = twoSeventyFive
        assertEquals("1", rt.calcReadingTime(), "275")

        rt.text = "$twoSeventyFive $twoSeventyFive"
        assertEquals("2", rt.calcReadingTime(), "275 * 2")
    }

    @Test
    fun testRoundingMode() {
        rt.text = blogPost
        rt.roundingMode = RoundingMode.UP
        assertEquals("3 min read", rt.calcReadingTime(), "UP")

        rt.text = mediumPost
        rt.roundingMode = RoundingMode.DOWN
        assertEquals("1 min read", rt.calcReadingTime(), "DOWN")
    }
}
