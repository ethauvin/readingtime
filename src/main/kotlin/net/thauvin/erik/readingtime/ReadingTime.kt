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

import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Computes the estimated reading time for a block of text.
 *
 * The calculation follows Medium’s approach, combining word count,
 * optional image time, and any additional seconds defined by the caller.
 */
class ReadingTime @JvmOverloads constructor(
    text: String,
    wpm: Int = DEFAULT_WPM,
    suffix: String = DEFAULT_SUFFIX,
    pluralSuffix: String = DEFAULT_PLURAL_SUFFIX,
    excludeImages: Boolean = false,
    extraSeconds: Int = 0,
    roundingMode: RoundingMode = DEFAULT_ROUNDING_MODE
) {
    constructor(config: ReadingTimeConfig) : this(
        config.text,
        config.wpm,
        config.suffix,
        config.pluralSuffix,
        config.excludeImages,
        config.extraSeconds,
        config.roundingMode
    )

    companion object {
        /**
         * Default reading speed in words per minute.
         */
        const val DEFAULT_WPM = 275

        /**
         * Default suffix.
         */
        const val DEFAULT_SUFFIX = "min read"

        /**
         * Default plural suffix.
         */
        const val DEFAULT_PLURAL_SUFFIX = "min read"

        /**
         * Default rounding mode.
         */
        val DEFAULT_ROUNDING_MODE: RoundingMode = RoundingMode.HALF_EVEN

        private const val INVALID: Double = -1.0

        /**
         * Counts the number of words in the given text.
         * HTML tags are removed before counting.
         */
        @JvmStatic
        fun countWords(text: String): Int {
            val stripped = Jsoup.parse(text).text().trim()
            return if (stripped.isBlank()) 0 else stripped.split("\\s+".toRegex()).size
        }

        /**
         * Counts HTML img tags.
         */
        @JvmStatic
        fun countImages(html: String): Int {
            return "<img\\b".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        }
    }

    private var cachedSeconds: Double = INVALID

    var text: String = text
        set(value) {
            reset(value != field)
            field = value
        }

    var wpm: Int = wpm
        set(value) {
            reset(value != field)
            field = value
        }

    var excludeImages: Boolean = excludeImages
        set(value) {
            reset(value != field)
            field = value
        }

    var extraSeconds: Int = extraSeconds
        set(value) {
            reset(value != field)
            field = value
        }

    var suffix: String = suffix
        set(value) {
            reset(value != field)
            field = value
        }

    var pluralSuffix: String = pluralSuffix
        set(value) {
            reset(value != field)
            field = value
        }

    var roundingMode: RoundingMode = roundingMode
        set(value) {
            reset(value != field)
            field = value
        }

    fun calcReadingTimeInSec(): Double {
        if (cachedSeconds == INVALID) {
            var total = 0.0
            if (!excludeImages) {
                total += calcImgReadingTime().toDouble()
            }
            total += (countWords(text).toDouble() / wpm.toDouble()) * 60.0
            cachedSeconds = total
        }
        return cachedSeconds + extraSeconds
    }

    fun calcReadingTimeInMin(): Int {
        val minutes = BigDecimal.valueOf(calcReadingTimeInSec() / 60.0)
            .setScale(0, roundingMode)
        return minutes.toInt()
    }

    fun calcReadingTime(): String {
        val minutes = calcReadingTimeInMin()
        return if (minutes > 1) {
            "$minutes $pluralSuffix".trim()
        } else {
            "$minutes $suffix".trim()
        }
    }

    fun imgCount(): Int = countImages(text)

    fun wordCount(): Int = countWords(text)

    private fun calcImgReadingTime(): Int {
        val count = countImages(text)
        var time = 0
        var offset = 12

        for (i in 1..count) {
            time += if (i > 10) 3 else offset--
        }

        return time
    }

    private fun reset(changed: Boolean) {
        if (changed) {
            cachedSeconds = INVALID
        }
    }
}