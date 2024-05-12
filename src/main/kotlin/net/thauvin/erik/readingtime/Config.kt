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

import java.math.RoundingMode

/**
 * Provides a configuration builder.
 */
class Config private constructor(
    val text: String,
    val wpm: Int,
    val postfix: String,
    val plural: String,
    val excludeImages: Boolean,
    val extra: Int,
    val roundingMode: RoundingMode
) {
    /**
     * Configures the parameters.
     */
    data class Builder(
        private var text: String = "",
        private var wpm: Int = 275,
        private var postfix: String = "min read",
        private var plural: String = "min read",
        private var excludeImages: Boolean = false,
        private var extra: Int = 0,
        private var roundingMode: RoundingMode = RoundingMode.HALF_EVEN
    ) {
        fun text(text: String) = apply { this.text = text }
        fun wpm(wpm: Int) = apply { this.wpm = wpm }
        fun postfix(postfix: String) = apply { this.postfix = postfix }
        fun plural(plural: String) = apply { this.plural = plural }
        fun excludeImages(excludeImages: Boolean) = apply { this.excludeImages = excludeImages }
        fun extra(extra: Int) = apply { this.extra = extra }
        fun roundingMode(roundingMode: RoundingMode) = apply { this.roundingMode = roundingMode }

        fun build() = Config(text, wpm, postfix, plural, excludeImages, extra, roundingMode)
    }
}
