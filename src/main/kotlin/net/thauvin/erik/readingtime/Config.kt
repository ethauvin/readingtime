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

import java.math.RoundingMode

/**
 * Configuration class for reading time calculations.
 *
 * @property text The text to be evaluated.
 * @property wpm The words per minute reading average.
 * @property postfix The text to append after the reading time.
 * @property plural The text to append after the reading time if plural.
 * @property excludeImages Whether to exclude images in the word count.
 * @property extra Additional time to add to the reading time.
 * @property roundingMode The rounding mode to apply to the reading time.
 */
class Config private constructor(builder: Builder) {
    val text = builder.text
    val wpm = builder.wpm
    val postfix = builder.postfix
    val plural = builder.plural
    val excludeImages = builder.excludeImages
    val extra = builder.extra
    val roundingMode = builder.roundingMode

    /**
     * Configures the parameters.
     *
     * @param text The text to be evaluated.
     */
    data class Builder(var text: String) {
        var wpm: Int = 275
        var postfix: String = "min read"
        var plural: String = "min read"
        var excludeImages: Boolean = false
        var extra: Int = 0
        var roundingMode: RoundingMode = RoundingMode.HALF_EVEN

        /**
         * The text to be evaluated.
         */
        fun text(text: String): Builder = apply { this.text = text }

        /**
         * The words per minute reading average.
         */
        fun wpm(wpm: Int): Builder = apply { this.wpm = wpm }

        /**
         * The value to be appended to the reading time.
         */
        fun postfix(postfix: String): Builder = apply { this.postfix = postfix }

        /**
         * The value to be appended if the reading time is more than 1 minute.
         */
        fun plural(plural: String): Builder = apply { this.plural = plural }

        /**
         * Images are excluded from the reading time when set.
         */
        fun excludeImages(excludeImages: Boolean): Builder = apply { this.excludeImages = excludeImages }

        /**
         * Additional seconds to be added to the total reading time.
         */
        fun extra(extra: Int): Builder = apply { this.extra = extra }

        /**
         * The [RoundingMode] to apply. Default is [RoundingMode.HALF_DOWN].
         */
        fun roundingMode(roundingMode: RoundingMode): Builder = apply { this.roundingMode = roundingMode }

        /**
         * Builds the configuration.
         */
        fun build(): Config = Config(this)
    }
}
