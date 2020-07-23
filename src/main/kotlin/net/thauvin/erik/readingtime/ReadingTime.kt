package net.thauvin.erik.readingtime

import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calculates the reading time of the given [text].
 *
 * Calculation based on this [Medium's Post](https://blog.medium.com/read-time-and-you-bc2048ab620c)
 */
class ReadingTime(
    var text: String,
    var wpm: Int = 275,
    var postfix: String = "min read",
    var plural: String = "min read",
    var excludeImages: Boolean = false
) {
    companion object {
        @JvmStatic
        fun wordCount(words: String): Int {
            val s = Jsoup.parse(words).text().trim()
            return if (s.isEmpty()) 0 else s.split("\\s+".toRegex()).size
        }

        @JvmStatic
        fun imgCount(html: String): Int {
            return "<img ".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        }
    }

    fun calcReadingTimeInSec(): Double {
        var readingTime = 0.0

        if (!excludeImages)
            readingTime += calcImgReadingTime()

        readingTime += wordCount(text) / (wpm / 60.0)

        return readingTime
    }

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
