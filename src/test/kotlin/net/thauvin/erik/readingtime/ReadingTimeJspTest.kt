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

import org.apache.catalina.WebResourceRoot
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.DirResourceSet
import org.apache.catalina.webresources.StandardRoot
import org.junit.jupiter.api.Test
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Embedded Tomcat JSP renderer used by all JSP tests.
 */
object TomcatJspTestHarness {

    fun renderJsp(jspName: String): String {
        val baseDir = File("build/tomcat").apply { mkdirs() }
        val tomcat = Tomcat().apply {
            setBaseDir(baseDir.absolutePath)
            setPort(0) // random port
        }

        // Webapp root = test JSP directory
        val webappDir = File("src/test/resources/jsp").absolutePath
        val ctx = tomcat.addWebapp("", webappDir)

        // Mount src/main/resources → /WEB-INF/classes
        val resources: WebResourceRoot = StandardRoot(ctx)
        resources.addPreResources(
            DirResourceSet(
                resources,
                "/WEB-INF/classes",
                File("src/main/resources").absolutePath,
                "/"
            )
        )
        ctx.resources = resources

        // Required for JSP compilation
        ctx.servletContext.setAttribute(
            "jakarta.servlet.context.tempdir",
            File("build/tomcat-tmp").apply { mkdirs() }
        )

        tomcat.start()

        val port = tomcat.connector.localPort
        val url = URL("http://localhost:$port/$jspName")
        val conn = url.openConnection() as HttpURLConnection
        val output = conn.inputStream.bufferedReader().readText()

        tomcat.stop()

        return output.trim()
    }
}

/**
 * Full test suite for all JSPs.
 */
class ReadingTimeJspTests {
    private val sampleTxt by lazy { File("src/test/resources/jsp/sample.txt").readText() }
    private val sampleHtml by lazy { File("src/test/resources/jsp/sample.html").readText() }


    @Test
    fun basicJsp() {
        val out = TomcatJspTestHarness.renderJsp("basic.jsp")
        val rt = ReadingTime(sampleTxt)
        assertEquals(rt.calcReadingTime(), out)
    }

    @Test
    fun wpmJsp() {
        val out = TomcatJspTestHarness.renderJsp("wpm.jsp")
        val rt = ReadingTimeEstimator.create(sampleTxt, 10)
        assertEquals(rt.readingTime(), out)
    }

    @Test
    fun suffixJsp() {
        val out = TomcatJspTestHarness.renderJsp("suffix.jsp")
        val rt = ReadingTimeEstimator.create(sampleTxt, "minute read")
        assertEquals(rt.readingTime(), out)
    }

    @Test
    fun pluralSuffixJsp() {
        val out = TomcatJspTestHarness.renderJsp("pluralSuffix.jsp")
        val rt = ReadingTimeEstimator.create(sampleTxt, 100, "minute read", "minutes read")
        assertEquals(rt.readingTime(), out)
    }

    @Test
    fun excludeImagesJsp() {
        val out = TomcatJspTestHarness.renderJsp("excludeImages.jsp")
        val rt = ReadingTime(sampleHtml, excludeImages = true)
        assertEquals(rt.calcReadingTime(), out)
    }

    @Test
    fun extraSecondsJsp() {
        val out = TomcatJspTestHarness.renderJsp("extraSeconds.jsp")
        val rt = ReadingTime(sampleTxt, extraSeconds = 120)
        assertEquals(rt.calcReadingTime(), out)
    }

    @Test
    fun debugJsp() {
        val out = TomcatJspTestHarness.renderJsp("debug.jsp")
        assertTrue(out.contains("<!--"))
        assertTrue(out.contains("body:"))
        assertTrue(out.contains("wpm:"))
    }
}
