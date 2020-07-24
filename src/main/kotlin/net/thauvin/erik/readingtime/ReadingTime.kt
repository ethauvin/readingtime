/*
 * ReadingTime.kt
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

import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calculates the reading time of the given [text].
 *
 * Based on [Medium's calculation](https://blog.medium.com/read-time-and-you-bc2048ab620c).
 *
 * @param text The text to be evaluated.
 * @param wpm The words per minute reading average.
 * @param postfix The value to be appended to the reading time.
 * @param plural The value to be appended if the reading time is more than 1 minute.
 * @param excludeImages Images are excluded from the reading time when set.
 */
class ReadingTime @JvmOverloads constructor(
    var text: String,
    var wpm: Int = 275,
    var postfix: String = "min read",
    var plural: String = "min read",
    var excludeImages: Boolean = false
) {
    companion object {
        /**
         * Counts words.
         *
         * HTML tags are stripped.
         */
        @JvmStatic
        fun wordCount(words: String): Int {
            val s = Jsoup.parse(words).text().trim()
            return if (s.isEmpty()) 0 else s.split("\\s+".toRegex()).size
        }

        /**
         * Counts HTML img tags.
         */
        @JvmStatic
        fun imgCount(html: String): Int {
            return "<img ".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        }
    }

    /**
     * Calculates and returns the reading time in seconds.
     */
    fun calcReadingTimeInSec(): Double {
        var readingTime = 0.0

        if (!excludeImages)
            readingTime += calcImgReadingTime()

        readingTime += wordCount(text) / (wpm / 60.0)

        return readingTime
    }

    /**
     * Calculates and returns the reading time. (eg. 1 min read)
     */
    fun calcReadingTime(): String {
        val readingTime = BigDecimal((calcReadingTimeInSec() / 60.0)).setScale(0, RoundingMode.CEILING)
        return if (readingTime.compareTo(BigDecimal.ONE) == 1) {
            "$readingTime $plural".trim()
        } else {
            "$readingTime $postfix".trim()
        }
    }

    private fun calcImgReadingTime(): Int {
        var imgTime = 0
        val imgCount = imgCount(text)

        var offset = 12
        for (i in 1..imgCount) {
            if (i > 10) {
                imgTime += 3
            } else {
                imgTime += offset
                offset--
            }
        }

        return imgTime
    }
}
