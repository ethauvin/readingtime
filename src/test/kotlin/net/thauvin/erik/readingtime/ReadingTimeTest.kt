/*
 * Copyright (c) 2020-2025, Erik C. Thauvin (erik@thauvin.net)
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

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.io.File
import java.math.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingTimeTest {
    private val sampleImgTag = """<img src="#">"""
    private val sampleReadingTime = ReadingTime("This is a <b>test</b>.\nWith an image: $sampleImgTag")
    private val blogPost by lazy { File("src/test/resources/post.html").readText() }
    private val mediumPost by lazy { File("src/test/resources/medium.html").readText() }
    private val textWith275Words = "word ".repeat(275)

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

    @Nested
    @DisplayName("Config Tests")
    inner class ConfigTests {
        private var config = Config.Builder(blogPost).plural("mins read")

        @Test
        fun blogPost() {
            assertEquals(
                "2 mins read", ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun emptyPlural() {
            config.text(mediumPost).plural("")
            assertEquals("2", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun emptyPostfix() {
            config.text("This is a test.").postfix("")
            assertEquals("0", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun emptyText() {
            config.text("")
            assertEquals("0 min read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun excludeImages() {
            config.excludeImages(true)
            assertEquals("1 min read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun extra() {
            config.extra(60)
            assertEquals("3 mins read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun plural() {
            config.plural("minutes read")
            assertEquals("2 minutes read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun postfix() {
            config.text("This is a test.").postfix("minute read")
            assertEquals("0 minute read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun roundingMode() {
            config.text(blogPost).roundingMode(RoundingMode.UP)
            assertEquals("3 mins read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun with275Words() {
            config.text(textWith275Words)
            assertEquals("1 min read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun with550Words() {
            config.text("$textWith275Words $textWith275Words")
            assertEquals("2 mins read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun wpm() {
            config.text(mediumPost).wpm(300).extra(0)
            assertEquals(
                calcReadingTime(mediumPost, 300) + calcImgTime(3),
                ReadingTime(config.build()).calcReadingTimeInSec()
            )
        }
    }

    @Nested
    @DisplayName("Image Count Tests")
    inner class ImageCountTests {
        @Test
        fun blogPost() {
            assertEquals(11, ReadingTime.imgCount(blogPost))
        }

        @Test
        fun imgCount() {
            assertEquals(1, ReadingTime.imgCount(sampleReadingTime.text))
        }

        @Test
        fun mediumPost() {
            assertEquals(3, ReadingTime.imgCount(mediumPost))
        }

        @Test
        fun with275WordsAndImage() {
            assertEquals(1, ReadingTime.imgCount("$textWith275Words $sampleImgTag"))
        }

        @Test
        fun with275WordsAnd2Images() {
            assertEquals(
                2,
                ReadingTime.imgCount("$textWith275Words $sampleImgTag $sampleImgTag"),
                "imgCount(275 + 2 images)"
            )
        }
    }

    @Nested
    @DisplayName("Reading Time In Sec Tests")
    inner class ReadingTimeInSecTests {
        @Test
        fun blogPost() {
            sampleReadingTime.text = blogPost
            assertEquals(
                calcReadingTime(sampleReadingTime.text, sampleReadingTime.wpm) + calcImgTime(11),
                sampleReadingTime.calcReadingTimeInSec()
            )
        }

        @Test
        fun blogPostExcludingImages() {
            sampleReadingTime.text = blogPost
            sampleReadingTime.excludeImages = true
            assertEquals(
                calcReadingTime(sampleReadingTime.text, sampleReadingTime.wpm),
                sampleReadingTime.calcReadingTimeInSec()
            )
        }

        @Test
        fun equalsReadingTimeInSec() {
            sampleReadingTime.text = "$textWith275Words $textWith275Words"
            sampleReadingTime.plural = ""
            assertEquals(
                sampleReadingTime.calcReadingTime().toInt() * 60,
                sampleReadingTime.calcReadingTimeInSec().toInt()
            )
        }

        @Test
        fun excludingImages() {
            sampleReadingTime.text = "$sampleImgTag $sampleImgTag"
            sampleReadingTime.excludeImages = true
            assertEquals(0.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun shortText() {
            sampleReadingTime.text = "This is a test"
            assertEquals(0.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with1Image() {
            assertEquals(
                calcReadingTime(sampleReadingTime.text, sampleReadingTime.wpm) + calcImgTime(1),
                sampleReadingTime.calcReadingTimeInSec()
            )
        }

        @Test
        fun with2Images() {
            sampleReadingTime.text = "$sampleImgTag ${sampleImgTag.uppercase()}"
            assertEquals(calcImgTime(2), sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with10Images() {
            sampleReadingTime.text = "$textWith275Words ${sampleImgTag.repeat(10)}"
            assertEquals(
                60.0 + 12 + 11 + 10 + 9 + 8 + 7 + 6 + 5 + 4 + 3,
                sampleReadingTime.calcReadingTimeInSec()
            )
        }

        @Test
        fun with11Images() {
            sampleReadingTime.text = "$textWith275Words ${sampleImgTag.repeat(10)} $sampleImgTag"
            assertEquals(135.0 + 3, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with275Words() {
            sampleReadingTime.text = textWith275Words
            assertEquals(60.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with275WordsAndImage() {
            sampleReadingTime.text = "$textWith275Words $sampleImgTag"
            assertEquals(72.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with275WordsAnd2Images() {
            sampleReadingTime.text = "$textWith275Words $sampleImgTag $sampleImgTag"
            assertEquals(83.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun with300Wpm() {
            sampleReadingTime.text = mediumPost
            sampleReadingTime.wpm = 300
            assertEquals(
                calcReadingTime(sampleReadingTime.text, 300) + calcImgTime(3),
                sampleReadingTime.calcReadingTimeInSec()
            )
        }

        @Test
        fun with550Words() {
            sampleReadingTime.text = "$textWith275Words $textWith275Words"
            assertEquals(120.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun withEmptyText() {
            sampleReadingTime.text = ""
            assertEquals(0.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun withExtra() {
            sampleReadingTime.text = textWith275Words
            sampleReadingTime.extra = 60
            assertEquals(
                calcReadingTime(sampleReadingTime.text, sampleReadingTime.wpm) + 60L,
                sampleReadingTime.calcReadingTimeInSec()
            )
        }
    }

    @Nested
    @DisplayName("Reading Time Tests")
    inner class ReadingTimeTests {
        @Test
        fun blogPost() {
            sampleReadingTime.text = blogPost
            assertEquals("2 min read", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun emptyPlural() {
            sampleReadingTime.text = mediumPost
            sampleReadingTime.plural = ""
            assertEquals("2", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun emptyPostfix() {
            sampleReadingTime.text = "This is a test."
            sampleReadingTime.postfix = ""
            assertEquals("0", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun emptyText() {
            sampleReadingTime.text = ""
            sampleReadingTime.postfix = ""
            assertEquals("0", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun plural() {
            sampleReadingTime.text = blogPost
            sampleReadingTime.plural = "mins read"
            assertEquals("2 mins read", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun with275Words() {
            sampleReadingTime.text = textWith275Words
            assertEquals("1 min read", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun with550Words() {
            sampleReadingTime.text = "$textWith275Words $textWith275Words"
            sampleReadingTime.plural = "mins read"
            assertEquals("2 mins read", sampleReadingTime.calcReadingTime())
        }
    }

    @Nested
    @DisplayName("Rounding Mode Tests")
    inner class RoundingModeTests {
        @Test
        fun roundingModeDown() {
            sampleReadingTime.text = mediumPost
            sampleReadingTime.roundingMode = RoundingMode.DOWN
            assertEquals("1 min read", sampleReadingTime.calcReadingTime(), "RoundingMode.DOWN")
        }

        @Test
        fun roundingModeUp() {
            sampleReadingTime.text = blogPost
            sampleReadingTime.roundingMode = RoundingMode.UP
            assertEquals("3 min read", sampleReadingTime.calcReadingTime(), "RoundingMode.UP")
        }
    }

    @Nested
    @DisplayName("Word Count Tests")
    inner class WordCountTests {
        @Test
        fun blogPost() {
            assertEquals(505, ReadingTime.wordCount(blogPost))
        }

        @Test
        fun empty() {
            assertEquals(0, ReadingTime.wordCount(" "))
        }

        @Test
        fun mediumPost() {
            assertEquals(391, ReadingTime.wordCount(mediumPost))
        }

        @Test
        fun with275Words() {
            assertEquals(275, ReadingTime.wordCount(textWith275Words))
        }

        @Test
        fun with275WordsAndImage() {
            assertEquals(275, ReadingTime.wordCount("$textWith275Words $sampleImgTag"))
        }

        @Test
        fun withSampleText() {
            assertEquals(7, ReadingTime.wordCount(sampleReadingTime.text))
        }

        @Test
        fun withSpaces() {
            assertEquals(2, ReadingTime.wordCount("    one    two    "))
        }

        @Test
        fun wordCount() {
            assertEquals(3, ReadingTime.wordCount("one two three"))
        }
    }
}
