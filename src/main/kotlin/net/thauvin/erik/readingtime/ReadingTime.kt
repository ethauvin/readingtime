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
 * The calculation follows Medium’s model: word count, optional image time,
 * and any additional seconds supplied by the caller. HTML content is allowed;
 * tags are stripped for word counting, while `<img>` tags contribute to image
 * time unless `excludeImages` is enabled.
 *
 * Results are cached and automatically invalidated when relevant properties change.
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
    /**
     * Creates a new instance using values from the provided [ReadingTimeConfig].
     */
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
        /** Default reading speed in words per minute. */
        const val DEFAULT_WPM = 275

        /** Default suffix used when the reading time is singular. */
        const val DEFAULT_SUFFIX = "min read"

        /** Default suffix used when the reading time is plural. */
        const val DEFAULT_PLURAL_SUFFIX = "min read"

        /** Default rounding mode applied when converting seconds to minutes. */
        val DEFAULT_ROUNDING_MODE: RoundingMode = RoundingMode.HALF_EVEN

        private const val INVALID: Double = -1.0

        /**
         * Counts the number of words in the given [text].
         *
         * HTML tags are removed before counting. Consecutive whitespace is treated
         * as a single separator. Returns `0` for blank or tag‑only content.
         */
        @JvmStatic
        fun countWords(text: String): Int {
            val stripped = Jsoup.parse(text).text().trim()
            return if (stripped.isBlank()) 0 else stripped.split("\\s+".toRegex()).size
        }

        /**
         * Counts the number of `<img>` tags in the given HTML [html].
         *
         * Matching is case‑insensitive and does not require closing tags.
         */
        @JvmStatic
        fun countImages(html: String): Int {
            return "<img\\b".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        }
    }

    /**
     * The text used to compute reading time. May contain HTML.
     * Changing this value invalidates cached results.
     */
    var text: String = text
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Words‑per‑minute used for estimation.
     * Changing this value invalidates cached results.
     */
    var wpm: Int = wpm
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * When `true`, image time is excluded from the calculation.
     * Changing this value invalidates cached results.
     */
    var excludeImages: Boolean = excludeImages
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Additional seconds added to the computed reading time.
     * Changing this value invalidates cached results.
     */
    var extraSeconds: Int = extraSeconds
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Suffix used when the reading time is singular (e.g., `"min read"`).
     */
    var suffix: String = suffix
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Suffix used when the reading time is plural (e.g., `"mins read"`).
     */
    var pluralSuffix: String = pluralSuffix
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Rounding mode applied when converting seconds to minutes.
     */
    var roundingMode: RoundingMode = roundingMode
        set(value) {
            reset(value != field)
            field = value
        }

    /**
     * Returns the estimated reading time in seconds.
     *
     * Image time is included unless `excludeImages` is `true`.
     * The result is cached and recomputed only when relevant properties change.
     */
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

    /**
     * Returns the estimated reading time in whole minutes.
     *
     * The value is derived from [calcReadingTimeInSec] and rounded using
     * the configured [roundingMode].
     */
    fun calcReadingTimeInMin(): Int {
        val minutes = BigDecimal.valueOf(calcReadingTimeInSec() / 60.0)
            .setScale(0, roundingMode)
        return minutes.toInt()
    }

    /**
     * Returns a formatted reading‑time string using either [suffix] or
     * [pluralSuffix] depending on the computed minute value.
     */
    fun calcReadingTime(): String {
        val minutes = calcReadingTimeInMin()
        return if (minutes > 1) {
            "$minutes $pluralSuffix".trim()
        } else {
            "$minutes $suffix".trim()
        }
    }

    /**
     * Returns the number of `<img>` tags in [text].
     */
    fun imgCount(): Int = countImages(text)

    /**
     * Returns the number of words in [text], with HTML removed.
     */
    fun wordCount(): Int = countWords(text)

    /**
     * Computes the image‑reading‑time component.
     *
     * Medium’s model assigns decreasing time for the first 10 images
     * (starting at 12 seconds), then 3 seconds for each additional image.
     */
    private fun calcImgReadingTime(): Int {
        val count = countImages(text)
        var time = 0
        var offset = 12

        for (i in 1..count) {
            time += if (i > 10) 3 else offset--
        }

        return time
    }

    private var cachedSeconds: Double = INVALID

    private fun reset(changed: Boolean) {
        if (changed) {
            cachedSeconds = INVALID
        }
    }
}