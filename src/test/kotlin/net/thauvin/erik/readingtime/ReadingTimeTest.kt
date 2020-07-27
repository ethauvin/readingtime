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

import net.thauvin.erik.readingtime.ReadingTime.Companion.imgCount
import net.thauvin.erik.readingtime.ReadingTime.Companion.wordCount
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingTimeTest {
    private val rt = ReadingTime("This is a <b>test</b>.\nWith an image: <img src=\"#\">")
    private val blogPost = File("src/test/resources/post.html").readText()
    private val mediumPost = File("src/test/resources/medium.html").readText()

    @Test
    fun testWordCount() {
        assertEquals(0, wordCount(" "))
        assertEquals(3, wordCount("one two three"))
        assertEquals(2, wordCount("    one    two    "))
        assertEquals(7, wordCount(rt.text))
        assertEquals(505, wordCount(blogPost))
        assertEquals(391, wordCount(mediumPost))
    }

    @Test
    fun testImgCount() {
        assertEquals(1, imgCount(rt.text))
        assertEquals(11, imgCount(blogPost))
        assertEquals(3, imgCount(mediumPost))
    }

    @Test
    fun testReadingTimeInSec() {
        assertEquals((wordCount(rt.text) / (rt.wpm / 60.0)) + 12.0, rt.calcReadingTimeInSec())

        rt.text = "<img src=\"#\"> <IMG src=\"#\">"
        assertEquals(12.0 + 11.0, rt.calcReadingTimeInSec())

        rt.text = blogPost
        assertEquals(
            (wordCount(rt.text) / (rt.wpm / 60.0)) + 12.0 + 11.0 + 10.0 + 9.0 + 8.0 + 7.0 + 6.0 + 5.0 + 4.0 + 3.0 + 3.0,
            rt.calcReadingTimeInSec()
        )

        rt.excludeImages = true
        assertEquals((wordCount(rt.text) / (rt.wpm / 60.0)), rt.calcReadingTimeInSec())
        rt.excludeImages = false

        rt.text = mediumPost
        rt.wpm = 300
        assertEquals(wordCount(rt.text) / (300.0 / 60.0) + 12.0 + 11.0 + 10.0, rt.calcReadingTimeInSec())
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
        assertEquals( 0.0, rt.calcReadingTimeInSec())
    }
}
