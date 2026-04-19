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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.RoundingMode

class ReadingTimeEstimatorTest {
    @Nested
    @DisplayName("create()")
    inner class Create {
        @Nested
        @DisplayName("create(text)")
        inner class CreateText {
            @Test
            fun createsWithDefaults() {
                val est = ReadingTimeEstimator.create("Hello world")
                val result = est.readingTime()
                assertTrue(result.isNotEmpty())
            }
        }

        @Nested
        @DisplayName("create(text, wpm)")
        inner class CreateTextWpm {
            @Test
            fun usesProvidedWpm() {
                val count = 400
                val est = ReadingTimeEstimator.create("word ".repeat(count), 200)
                val min = est.readingTimeInMinutes()
                assertTrue(min >= 2)
                assertEquals(count, est.wordCount())
            }
        }

        @Nested
        @DisplayName("create(text, suffix)")
        inner class CreateTextSuffix {
            @Test
            fun appliesSuffixToBothForms() {
                val est = ReadingTimeEstimator.create("Hello world", "minute read")
                val result = est.readingTime()
                assertTrue(result.endsWith("minute read"))
            }
        }

        @Nested
        @DisplayName("create(text, suffix, pluralSuffix)")
        inner class CreateTextSuffixPlural {
            @Test
            fun appliesBothSuffix() {
                val est = ReadingTimeEstimator.create(
                    "word ".repeat(600),
                    "minute read",
                    "minutes read"
                )
                val result = est.readingTime()
                assertTrue(result.endsWith("minutes read"))
            }
        }

        @Nested
        @DisplayName("create(text, wpm, suffix)")
        inner class CreateTextWpmSuffix {
            @Test
            fun appliesSuffixAndWpm() {
                val est = ReadingTimeEstimator.create("word ".repeat(300), 300, "minute read")
                val result = est.readingTime()
                assertTrue(result.endsWith("minute read"))
            }
        }

        @Nested
        @DisplayName("create(text, wpm, suffix, pluralSuffix)")
        inner class CreateTextWpmSuffixPlural {
            @Test
            fun appliesPluralSuffix() {
                val est = ReadingTimeEstimator.create(
                    "word ".repeat(600),
                    200,
                    "minute read",
                    "minutes read"
                )
                val result = est.readingTime()
                assertTrue(result.endsWith("minutes read"))
            }
        }

        @Nested
        @DisplayName("create(full explicit)")
        inner class CreateFullExplicit {
            @Test
            fun usesAllExplicitValues() {
                val est = ReadingTimeEstimator.create(
                    "word ".repeat(300),
                    250,
                    "min",
                    "mins",
                    true,
                    5,
                    RoundingMode.HALF_UP
                )
                val result = est.readingTime()
                assertTrue(result.endsWith("min") || result.endsWith("mins"))
            }
        }

        @Test
        fun createsEstimatorWithExplicitValues() {
            val est = ReadingTimeEstimator.create(
                "Hello world",
                200,
                "minute read",
                "minutes read",
                false,
                0,
                RoundingMode.HALF_EVEN
            )
            val result = est.readingTime()
            assertTrue(result.contains("minute"))
        }

    }

    @Nested
    @DisplayName("DSL")
    inner class Dsl {
        @Test
        fun createsEstimatorWithDsl() {
            val est = readingTimeEstimator {
                text("Hello world")
                wpm(200)
                suffix("min read")
            }
            val result = est.readingTime()
            assertTrue(result.endsWith("min read"))
            assertEquals(2, est.wordCount())
        }
        @Test
        fun appliesPluralSuffixWithDsl() {
            val est = readingTimeEstimator {
                text("word ".repeat(600))
                suffixes("minute read", "minutes read")
            }
            val result = est.readingTime()
            assertTrue(result.endsWith("minutes read"))
        }

    }

    @Nested
    @DisplayName("fromConfig()")
    inner class FromConfig {
        @Test
        fun createsEstimatorFromConfig() {
            val config = ReadingTimeConfig.Builder("Hello world").build()
            val est = ReadingTimeEstimator.fromConfig(config)
            val result = est.readingTime()
            assertTrue(result.isNotEmpty())
        }
    }

    @Nested
    @DisplayName("readingTimeInSeconds()")
    inner class ReadingTimeInSeconds {
        @ParameterizedTest
        @ValueSource(strings = ["Hello world", "One two three four five"])
        fun calculatesSeconds(text: String) {
            val est = ReadingTimeEstimator.create(
                text,
                200,
                "min read",
                "min read",
                false,
                0,
                RoundingMode.HALF_EVEN
            )
            val sec = est.readingTimeInSeconds()
            assertTrue(sec > 0.0)
        }
    }

    @Nested
    @DisplayName("readingTimeInMinutes()")
    inner class ReadingTimeMinutes {
        @Test
        fun calculatesSeconds() {
            val est = ReadingTimeEstimator.create(
                "word ".repeat(276),
                200,
                "min read",
                "min read",
                false,
                0,
                RoundingMode.HALF_EVEN
            )
            val sec = est.readingTimeInMinutes()
            assertEquals(1, sec)
        }
    }

    @Nested
    @DisplayName("readingTime()")
    inner class ReadingTimeString {
        @Test
        fun formatsReadingTime() {
            val est = ReadingTimeEstimator.create(
                "This is a short sentence.",
                275,
                "minute read",
                "minutes read",
                false,
                0,
                RoundingMode.HALF_EVEN
            )
            val result = est.readingTime()
            assertTrue(result.endsWith("minute read"))
        }

        @Test
        fun usesPluralSuffixWhenNeeded() {
            val longText = "word ".repeat(600)
            val est = ReadingTimeEstimator.create(
                longText,
                200,
                "min read",
                "mins read",
                false,
                0,
                RoundingMode.HALF_EVEN
            )
            val result = est.readingTime()
            assertTrue(result.endsWith("mins read"))
        }
    }

    @Nested
    @DisplayName("Words and Images Count")
    inner class WordsAndImagesCount {
        @Test
        fun countsWords() {
            val count = 301
            val est = ReadingTimeEstimator.create("word ".repeat(count))
            assertEquals(count, est.wordCount())
        }

        @Test
        fun countsImages() {
            val est = ReadingTimeEstimator.create("<img src='#'>foo<img src='#'>")
            assertEquals(2, est.imgCount())
        }
    }
}
