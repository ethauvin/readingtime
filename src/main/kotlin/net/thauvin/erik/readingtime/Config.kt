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

import java.math.RoundingMode

/**
 * Immutable configuration for computing reading time.
 *
 * Defines the input text, reading speed, suffixes, and optional adjustments
 * such as image exclusion, extra seconds, and rounding behavior.
 */
class Config private constructor(builder: Builder) {
    val text: String = builder.text
    val wpm: Int = builder.wpm
    val suffix: String = builder.suffix
    val pluralSuffix: String = builder.pluralSuffix
    val excludeImages: Boolean = builder.excludeImages
    val extraSeconds: Int = builder.extraSeconds
    val roundingMode: RoundingMode = builder.roundingMode

    /**
     * Builder for [Config].
     *
     * The only required value is the input text. All other fields have defaults.
     */
    data class Builder(var text: String) {
        var wpm: Int = 275
        var suffix: String = "min read"
        var pluralSuffix: String = "min read"
        var excludeImages: Boolean = false
        var extraSeconds: Int = 0
        var roundingMode: RoundingMode = RoundingMode.HALF_EVEN

        /**
         * Sets the input text to evaluate.
         */
        fun text(text: String): Builder = apply { this.text = text }

        /**
         * Sets the words‑per‑minute reading speed.
         */
        fun wpm(wpm: Int): Builder = apply { this.wpm = wpm }

        /**
         * Sets the suffix appended to the reading time.
         */
        fun suffix(suffix: String): Builder = apply { this.suffix = suffix }

        /**
         * Sets the suffix used when the reading time is plural.
         */
        fun pluralSuffix(pluralSuffix: String): Builder = apply { this.pluralSuffix = pluralSuffix }

        /**
         * Excludes images from the reading time calculation when true.
         */
        fun excludeImages(excludeImages: Boolean): Builder = apply { this.excludeImages = excludeImages }

        /**
         * Adds extra seconds to the computed reading time.
         */
        fun extraSeconds(extraSeconds: Int): Builder = apply { this.extraSeconds = extraSeconds }

        /**
         * Sets the rounding mode applied to the final reading time.
         */
        fun roundingMode(roundingMode: RoundingMode): Builder = apply { this.roundingMode = roundingMode }

        /**
         * Builds the immutable configuration.
         */
        fun build(): Config = Config(this)
    }
}
