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

class ReadingTimeEstimator internal constructor(delegate: ReadingTime) {
    private val delegate = ReadingTime(
        delegate.text,
        delegate.wpm,
        delegate.suffix,
        delegate.pluralSuffix,
        delegate.excludeImages,
        delegate.extraSeconds,
        delegate.roundingMode
    )

    companion object {
        @JvmStatic
        fun fromConfig(config: ReadingTimeConfig): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(config))
        }

        @JvmStatic
        fun create(text: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(text))
        }

        @JvmStatic
        fun create(text: String, wpm: Int): ReadingTimeEstimator {
            return ReadingTimeEstimator(ReadingTime(text, wpm))
        }

        @JvmStatic
        fun create(text: String, suffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, suffix = suffix, pluralSuffix = suffix)
            )
        }

        @JvmStatic
        fun create(text: String, wpm: Int, suffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, wpm, suffix = suffix, pluralSuffix = suffix)
            )
        }

        @JvmStatic
        fun create(text: String, suffix: String, pluralSuffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, suffix = suffix, pluralSuffix = pluralSuffix)
            )
        }

        @JvmStatic
        fun create(text: String, wpm: Int, suffix: String, pluralSuffix: String): ReadingTimeEstimator {
            return ReadingTimeEstimator(
                ReadingTime(text, wpm, suffix, pluralSuffix)
            )
        }

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

    @JvmName("readingTimeInMinutes")
    fun readingTimeInMinutes(): Int {
        return delegate.calcReadingTimeInMin()
    }

    @JvmName("readingTimeInSeconds")
    fun readingTimeInSeconds(): Double {
        return delegate.calcReadingTimeInSec()
    }

    @JvmName("readingTime")
    fun readingTime(): String {
        return delegate.calcReadingTime()
    }

    @JvmName("wordCount")
    fun wordCount(): Int {
        return delegate.wordCount()
    }

    @JvmName("imgCount")
    fun imgCount(): Int {
        return delegate.imgCount()
    }
}

@DslMarker
annotation class ReadingTimeEstimatorDslMarker

@ReadingTimeEstimatorDslMarker
interface ReadingTimeEstimatorScope {
    fun text(value: String)
    fun wpm(value: Int)
    fun suffix(value: String)
    fun pluralSuffix(value: String)
    fun suffixes(singular: String, plural: String)
    fun excludeImages(value: Boolean)
    fun extraSeconds(value: Int)
    fun roundingMode(value: java.math.RoundingMode)
}

internal class ReadingTimeEstimatorDsl : ReadingTimeEstimatorScope {
    private var _text: String = ""
    private var _wpm: Int = ReadingTime.DEFAULT_WPM
    private var _suffix: String = ReadingTime.DEFAULT_SUFFIX
    private var _pluralSuffix: String = ReadingTime.DEFAULT_PLURAL_SUFFIX
    private var _excludeImages: Boolean = false
    private var _extraSeconds: Int = 0
    private var _roundingMode: java.math.RoundingMode = ReadingTime.DEFAULT_ROUNDING_MODE

    override fun text(value: String) {
        _text = value
    }

    override fun wpm(value: Int) {
        _wpm = value
    }

    override fun suffix(value: String) {
        _suffix = value
        _pluralSuffix = value
    }

    override fun pluralSuffix(value: String) {
        _pluralSuffix = value
    }

    override fun suffixes(singular: String, plural: String) {
        _suffix = singular
        _pluralSuffix = plural
    }

    override fun excludeImages(value: Boolean) {
        _excludeImages = value
    }

    override fun extraSeconds(value: Int) {
        _extraSeconds = value
    }

    override fun roundingMode(value: java.math.RoundingMode) {
        _roundingMode = value
    }

    fun build(): ReadingTimeEstimator {
        return ReadingTimeEstimator(
            ReadingTime(
                _text,
                _wpm,
                _suffix,
                _pluralSuffix,
                _excludeImages,
                _extraSeconds,
                _roundingMode
            )
        )
    }
}

/**
 * Creates a [ReadingTimeEstimator] using a Kotlin DSL.
 *
 * This provides an idiomatic way to configure all reading‑time parameters
 * without exposing implementation details. Hidden from Java callers.
 *
 * Example:
 *```
 * val est = readingTimeEstimator {
 *     text("This is a DSL example.")
 *     wpm(220)
 *     suffix("min")
 *     pluralSuffix("mins")
 *     excludeImages(true)
 *     extraSeconds(5)
 * }
 *```
 */
@JvmSynthetic
fun readingTimeEstimator(block: ReadingTimeEstimatorScope.() -> Unit): ReadingTimeEstimator {
    val dsl = ReadingTimeEstimatorDsl()
    dsl.block()
    return dsl.build()
}
