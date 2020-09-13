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
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingTimeTest {
    private val rt = ReadingTime("This is a <b>test</b>.\nWith an image: <img src=\"#\">")
    private val blogPost = File("src/test/resources/post.html").readText()
    private val mediumPost = File("src/test/resources/medium.html").readText()

    private fun calcImgTime(imgCount: Int): Double {
        var time = 0.0
        var offset = 12

        for (i in 1..imgCount) {
            time += offset
            if (offset != 3) {
                offset--
            }
        }
        return time
    }

    private fun calcReadingTime(text: String, wpm: Int): Double {
        return ReadingTime.wordCount(text) / (wpm / 60.0)
    }

    @Test
    fun testWordCount() {
        assertEquals(0, ReadingTime.wordCount(" "))
        assertEquals(3, ReadingTime.wordCount("one two three"))
        assertEquals(2, ReadingTime.wordCount("    one    two    "))
        assertEquals(7, ReadingTime.wordCount(rt.text))
        assertEquals(505, ReadingTime.wordCount(blogPost))
        assertEquals(391, ReadingTime.wordCount(mediumPost))
    }

    @Test
    fun testImgCount() {
        assertEquals(1, ReadingTime.imgCount(rt.text))
        assertEquals(11, ReadingTime.imgCount(blogPost))
        assertEquals(3, ReadingTime.imgCount(mediumPost))
    }

    @Test
    fun testReadingTimeInSec() {
        assertEquals(calcReadingTime(rt.text, rt.wpm) + calcImgTime(1), rt.calcReadingTimeInSec())

        rt.text = "<img src=\"#\"> <IMG src=\"#\">"
        assertEquals(calcImgTime(2), rt.calcReadingTimeInSec())
        rt.excludeImages = true
        assertEquals(0.0, rt.calcReadingTimeInSec())
        rt.excludeImages = false

        rt.text = blogPost
        assertEquals(
            calcReadingTime(rt.text, rt.wpm) + calcImgTime(11), rt.calcReadingTimeInSec()
        )

        rt.excludeImages = true
        assertEquals(calcReadingTime(rt.text, rt.wpm), rt.calcReadingTimeInSec())
        rt.extra = 60
        assertEquals(calcReadingTime(rt.text, rt.wpm) + 60L, rt.calcReadingTimeInSec())
        rt.extra = 0
        rt.excludeImages = false

        rt.text = mediumPost
        rt.wpm = 300
        assertEquals(calcReadingTime(rt.text, 300) + calcImgTime(3), rt.calcReadingTimeInSec())
        rt.wpm = 275
    }

    @Test
    fun testReadingTime() {
        rt.text = blogPost
        assertEquals("4 min read", rt.calcReadingTime())

        rt.plural = "mins read"
        assertEquals("4 mins read", rt.calcReadingTime())

        rt.text = mediumPost
        rt.plural = ""
        assertEquals("2", rt.calcReadingTime())

        rt.text = "This is a test."
        rt.postfix = ""
        assertEquals("1", rt.calcReadingTime())

        rt.text = ""
        assertEquals("0", rt.calcReadingTime())
        assertEquals(0.0, rt.calcReadingTimeInSec())
    }
}
