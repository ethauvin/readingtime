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

/**
 * Java‑friendly wrapper around [ReadingTime].
 *
 * Provides a stable, conventional API surface for Java callers
 * without exposing Kotlin‑specific patterns or default parameters.
 */
class ReadingTimeEstimator private constructor(
    private val delegate: ReadingTime
) {
    companion object {
        /**
         * Creates an estimator using the provided configuration.
         */
        @JvmStatic
        fun fromConfig(config: ReadingTimeConfig): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(config))
        }

        /**
         * Creates an estimator using only the input text.
         *
         * Uses default values for all other settings.
         */
        @JvmStatic
        fun create(text: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(text))
        }

        /**
         * Creates an estimator using the input text and reading speed.
         *
         * Uses default values for all other settings.
         */
        @JvmStatic
        fun create(text: String, wpm: Int): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(text, wpm))
        }

        /**
         * Creates an estimator using text and a singular suffix.
         *
         * Uses default values for all other settings and applies the same value
         * to the plural suffix.
         */
        @JvmStatic
        fun create(text: String, suffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, suffix = suffix, pluralSuffix = suffix)
            )
        }


        /**
         * Creates an estimator using text, reading speed, and a singular suffix.
         *
         * Uses the same value for the plural suffix and default values for all other settings.
         */
        @JvmStatic
        fun create(text: String, wpm: Int, suffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, wpm, suffix = suffix, pluralSuffix = suffix)
            )
        }

        /**
         * Creates an estimator using text and suffixes.
         *
         * Uses default values for all other settings.
         */
        @JvmStatic
        fun create(
            text: String,
            suffix: String,
            pluralSuffix: String
        ): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, suffix = suffix, pluralSuffix = pluralSuffix)
            )
        }

        /**
         * Creates an estimator using text, reading speed, and suffixes.
         *
         * Uses default values for all other settings.
         */
        @JvmStatic
        fun create(
            text: String,
            wpm: Int,
            suffix: String,
            pluralSuffix: String
        ): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, wpm, suffix, pluralSuffix)
            )
        }

        /**
         * Creates an estimator with full control over all settings.
         */
        @JvmStatic
        fun create(
            text: String,
            wpm: Int,
            suffix: String,
            pluralSuffix: String,
            excludeImages: Boolean,
            extraSeconds: Int,
            roundingMode: java.math.RoundingMode
        ): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(
                    text,
                    wpm,
                    suffix,
                    pluralSuffix,
                    excludeImages,
                    extraSeconds,
                    roundingMode
                )
            )
        }
    }

    /**
     * Returns the rounded reading time in minutes.
     *
     * Uses the configured rounding mode.
     */
    @JvmName("readingTimeInMinutes")
    fun readingTimeInMinutes(): Int {
        return delegate.calcReadingTimeInMin()
    }

    /**
     * Returns the total reading time in seconds.
     */
    @JvmName("readingTimeInSeconds")
    fun readingTimeInSeconds(): Double {
        return delegate.calcReadingTimeInSec()
    }

    /**
     * Returns the formatted reading time string.
     */
    @JvmName("readingTime")
    fun readingTime(): String {
        return delegate.calcReadingTime()
    }

    /**
     * Returns the word count.
     */
    @JvmName("wordCount")
    fun wordCount(): Int {
        return delegate.wordCount()
    }

    /**
     * Returns the number of images.
     */
    @JvmName("imgCount")
    fun imgCount(): Int {
        return delegate.imgCount()
    }
}
