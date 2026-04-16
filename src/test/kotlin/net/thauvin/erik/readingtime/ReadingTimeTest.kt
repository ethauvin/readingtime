/*
 * Copyright (c) 2020-2026, Erik C. Thauvin (erik@thauvin.net)
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

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.ValueSource
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
        return (ReadingTime.countWords(text).toDouble() / wpm.toDouble()) * 60.0
    }

    @Nested
    @DisplayName("Config Tests")
    inner class ConfigTests {
        private var config = Config.Builder(blogPost).pluralSuffix("mins read")

        @Test
        fun blogPost() {
            assertEquals(
                "3 mins read", ReadingTime(config.build()).calcReadingTime()
            )
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyPlural(input: String) {
            config.text(mediumPost).pluralSuffix(input)
            assertEquals("2", ReadingTime(config.build()).calcReadingTime())
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyPostfix(input: String) {
            config.text("This is a test.").suffix(input)
            assertEquals("0", ReadingTime(config.build()).calcReadingTime())
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyText(input: String) {
            config.text(input)
            assertEquals(
                "0 ${ReadingTime.DEFAULT_SUFFIX}",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun excludeImages() {
            config.excludeImages(true)
            assertEquals(
                "2 mins read",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun extraSeconds() {
            config.extraSeconds(60)
            assertEquals(
                "4 mins read",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun pluralSuffix() {
            config.pluralSuffix("minutes read")
            assertEquals("3 minutes read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun suffix() {
            config.text("This is a test.").suffix("minute read")
            assertEquals("0 minute read", ReadingTime(config.build()).calcReadingTime())
        }

        @Test
        fun roundingMode() {
            config.text(blogPost).roundingMode(RoundingMode.UP)
            assertEquals(
                "4 mins read",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun with275Words() {
            config.text(textWith275Words)
            assertEquals(
                "1 ${ReadingTime.DEFAULT_SUFFIX}",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun with550Words() {
            config.text("$textWith275Words $textWith275Words")
            assertEquals(
                "2 mins read",
                ReadingTime(config.build()).calcReadingTime()
            )
        }

        @Test
        fun wpm() {
            config.text(mediumPost).wpm(300).extraSeconds(0)
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
            assertEquals(11, ReadingTime.countImages(blogPost))
        }

        @Test
        fun countImages() {

            assertEquals(1, ReadingTime.countImages(sampleReadingTime.text))
        }

        @Test
        fun mediumPost() {
            val imgCount = ReadingTime(mediumPost).imgCount()
            assertEquals(3, imgCount)
            assertEquals(imgCount, ReadingTime.countImages(mediumPost))
        }

        @Test
        fun with275WordsAndImage() {
            assertEquals(1, ReadingTime.countImages("$textWith275Words $sampleImgTag"))
        }

        @Test
        fun with275WordsAnd2Images() {
            assertEquals(
                2,
                ReadingTime.countImages("$textWith275Words $sampleImgTag $sampleImgTag"),
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
            sampleReadingTime.pluralSuffix = ""
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
            assertEquals(0.8727272727272727, sampleReadingTime.calcReadingTimeInSec())
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

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun withEmptyText(input: String) {
            sampleReadingTime.text = input
            assertEquals(0.0, sampleReadingTime.calcReadingTimeInSec())
        }

        @Test
        fun withExtra() {
            sampleReadingTime.text = textWith275Words
            sampleReadingTime.extraSeconds = 60
            assertEquals(
                calcReadingTime(sampleReadingTime.text, sampleReadingTime.wpm) + 60L,
                sampleReadingTime.calcReadingTimeInSec()
            )
        }
    }

    @DisplayName("Reading Time In Min Tests")
    class ReadingTimeInMinTests {
        @Nested
        @DisplayName("basic rounding")
        inner class BasicRounding {
            @Test
            fun roundsToNearestMinute() {
                val text = "word ".repeat(300)
                val rt = ReadingTime(
                    text,
                    250,
                    "min read",
                    "min read",
                    false,
                    0,
                    RoundingMode.HALF_EVEN
                )
                val min = rt.calcReadingTimeInMin()
                assertTrue(min >= 1)
            }
        }

        @Nested
        @DisplayName("different rounding modes")
        inner class RoundingModes {
            @ParameterizedTest
            @ValueSource(strings = ["HALF_UP", "HALF_DOWN", "HALF_EVEN"])
            fun appliesRoundingMode(modeName: String) {
                val mode = RoundingMode.valueOf(modeName)
                val text = "word ".repeat(275)
                val rt = ReadingTime(
                    text,
                    275,
                    "min read",
                    "min read",
                    false,
                    0,
                    mode
                )
                val min = rt.calcReadingTimeInMin()
                assertTrue(min >= 1)
            }
        }

        @Nested
        @DisplayName("plural vs singular consistency")
        inner class Plurality {
            @Test
            fun returnsHigherValueForLongerText() {
                val shortText = "word ".repeat(100)
                val longText = "word ".repeat(800)
                val rtShort = ReadingTime(shortText)
                val rtLong = ReadingTime(longText)
                val shortMin = rtShort.calcReadingTimeInMin()
                val longMin = rtLong.calcReadingTimeInMin()
                assertTrue(longMin > shortMin)
            }
        }
    }


    @Nested
    @DisplayName("Reading Time Tests")
    inner class ReadingTimeTests {
        @Test
        fun blogPost() {
            sampleReadingTime.text = blogPost
            assertEquals("3 min read", sampleReadingTime.calcReadingTime())
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyPlural(input: String) {
            sampleReadingTime.text = mediumPost
            sampleReadingTime.pluralSuffix = input
            assertEquals("2", sampleReadingTime.calcReadingTime())
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyPostfix(input: String) {
            sampleReadingTime.text = "This is a test."
            sampleReadingTime.suffix = input
            assertEquals("0", sampleReadingTime.calcReadingTime())
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun emptyText(input: String) {
            sampleReadingTime.text = input
            sampleReadingTime.suffix = input
            assertEquals("0", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun pluralSuffix() {
            sampleReadingTime.text = blogPost
            sampleReadingTime.pluralSuffix = "mins read"
            assertEquals("3 mins read", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun with275Words() {
            sampleReadingTime.text = textWith275Words
            assertEquals("1 min read", sampleReadingTime.calcReadingTime())
        }

        @Test
        fun with550Words() {
            sampleReadingTime.text = "$textWith275Words $textWith275Words"
            sampleReadingTime.pluralSuffix = "mins read"
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
            assertEquals("4 min read", sampleReadingTime.calcReadingTime(), "RoundingMode.UP")
        }
    }

    @Nested
    @DisplayName("Word Count Tests")
    inner class WordCountTests {
        @Test
        fun blogPost() {
            assertEquals(505, ReadingTime.countWords(blogPost))
        }

        @Test
        fun countWords() {
            assertEquals(3, ReadingTime.countWords("one two three"))
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = [" ", "  "])
        fun empty(input: String) {
            assertEquals(0, ReadingTime.countWords(input))
        }

        @Test
        fun mediumPost() {
            assertEquals(391, ReadingTime.countWords(mediumPost))
        }

        @Test
        fun with275Words() {
            assertEquals(275, ReadingTime.countWords(textWith275Words))
        }

        @Test
        fun with275WordsAndImage() {
            assertEquals(275, ReadingTime.countWords("$textWith275Words $sampleImgTag"))
        }

        @Test
        fun withSampleText() {
            assertEquals(7, ReadingTime.countWords(sampleReadingTime.text))
        }

        @Test
        fun withSpaces() {
            assertEquals(2, ReadingTime.countWords("    one    two    "))
        }
    }
}
