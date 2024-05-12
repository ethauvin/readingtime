/*
 * Copyright (c) 2020-2024, Erik C. Thauvin (erik@thauvin.net)
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
 * Calculates the reading time of the given [text].
 *
 * Based on [Medium's calculation](https://blog.medium.com/read-time-and-you-bc2048ab620c).
 *
 * @constructor Constructs a new [ReadingTime] object.
 *
 * @param text The text to be evaluated.
 * @param wpm The words per minute reading average.
 * @param postfix The value to be appended to the reading time.
 * @param plural The value to be appended if the reading time is more than 1 minute.
 * @param excludeImages Images are excluded from the reading time when set.
 * @param extra Additional seconds to be added to the total reading time.
 * @param roundingMode The [RoundingMode] to apply. Default is [RoundingMode.HALF_DOWN].
 */
class ReadingTime @JvmOverloads constructor(
    text: String,
    wpm: Int = 275,
    var postfix: String = "min read",
    var plural: String = "min read",
    excludeImages: Boolean = false,
    extra: Int = 0,
    var roundingMode: RoundingMode = RoundingMode.HALF_EVEN
) {
    constructor(config: Config) : this(
        config.text,
        config.wpm,
        config.postfix,
        config.plural,
        config.excludeImages,
        config.extra,
        config.roundingMode
    )

    companion object {
        private const val INVALID: Double = -1.0

        /**
         * Counts words.
         *
         * HTML tags are stripped.
         */
        @JvmStatic
        fun wordCount(words: String): Int {
            val s = Jsoup.parse(words).text().trim()
            return if (s.isBlank()) 0 else s.split("\\s+".toRegex()).size
        }

        /**
         * Counts HTML img tags.
         */
        @JvmStatic
        fun imgCount(html: String): Int {
            return "<img ".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        }
    }

    private var readTime: Double = INVALID

    var text: String = text
        set(value) {
            reset(value != text)
            field = value
        }

    var wpm: Int = wpm
        set(value) {
            reset(value != wpm)
            field = value
        }

    var excludeImages: Boolean = excludeImages
        set(value) {
            reset(value != excludeImages)
            field = value
        }

    var extra: Int = extra
        set(value) {
            reset(value != extra)
            field = value
        }

    /**
     * Calculates and returns the reading time in seconds.
     *
     * `((word count / wpm) * 60) + images + extra`
     */
    fun calcReadingTimeInSec(): Double {
        if (readTime == INVALID) {
            readTime = if (!excludeImages) calcImgReadingTime().toDouble() else 0.0
            readTime += (wordCount(text) / wpm) * 60.0
        }

        return readTime + extra
    }

    /**
     * Calculates and returns the reading time. (e.g. 1 min read)
     *
     * `(reading time in sec / 60) + postfix`
     */
    fun calcReadingTime(): String {
        val time = BigDecimal((calcReadingTimeInSec() / 60.0)).setScale(0, roundingMode)
        return if (time.compareTo(BigDecimal.ONE) == 1) {
            "$time $plural".trim()
        } else {
            "$time $postfix".trim()
        }
    }

    /**
     * 12 seconds for the first image, 11 for the second, and minus an additional second for each subsequent image.
     * Any images after the tenth image are counted at 3 seconds.
     */
    private fun calcImgReadingTime(): Int {
        var time = 0
        val imgCount = imgCount(text)

        var offset = 12
        for (i in 1..imgCount) {
            if (i > 10) {
                time += 3
            } else {
                time += offset
                offset--
            }
        }

        return time
    }

    private fun reset(isChanged: Boolean) {
        if (isChanged) readTime = INVALID
    }
}
