package net.thauvin.erik.readingtime

import net.thauvin.erik.readingtime.ReadingTime.Companion.imgCount
import net.thauvin.erik.readingtime.ReadingTime.Companion.wordCount
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingTimeTest {
    private val rt = ReadingTime("This is a <b>test</b>.\nWith an image: <img src=\"#\">")
    private val blogPost = File("src/test/resources/post.html").readText()
    private val mediumPost = File("src/test/resources/medium.html").readText()

    @Test
    fun testWordCount() {
        assertEquals(0, wordCount(" "))
        assertEquals(3, wordCount("one two three"))
        assertEquals(2, wordCount("    one    two    "))
        assertEquals(7, wordCount(rt.text))
        assertEquals(210, wordCount(blogPost))
    }

    @Test
    fun testImgCount() {
        assertEquals(1, imgCount(rt.text))
        assertEquals(4, imgCount(blogPost))
        assertEquals(3, imgCount(mediumPost))
    }

    @Test
    fun testReadingTimeInSec() {
        assertEquals((wordCount(rt.text) / (rt.wpm / 60.0)) + 12.0, rt.calcReadingTimeInSec())
        rt.text = "<img src=\"#\"> <IMG src=\"#\">"
        assertEquals(12.0 + 11.0, rt.calcReadingTimeInSec())
        rt.text = blogPost
        assertEquals((wordCount(rt.text) / (rt.wpm / 60.0)) + 12.0 + 11.0 + 10.0 + 9.0, rt.calcReadingTimeInSec())
        rt.text = mediumPost
        rt.wpm = 300
        assertEquals(wordCount(rt.text) / (rt.wpm / 60.0) + 12.0 + 11.0 + 10.0, rt.calcReadingTimeInSec())
    }

    @Test
    fun testReadingTime() {
        rt.text = blogPost
        assertEquals("2 min read", rt.calcReadingTime())
        rt.plural = "mins read"
        assertEquals("2 mins read", rt.calcReadingTime())
        rt.text = mediumPost
        rt.plural = ""
        assertEquals("2", rt.calcReadingTime())
    }
}
