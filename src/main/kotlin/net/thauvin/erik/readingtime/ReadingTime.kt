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
class ReadingTime(
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
        val readingTime = BigDecimal((calcReadingTimeInSec() / 60)).setScale(0, RoundingMode.CEILING)
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
