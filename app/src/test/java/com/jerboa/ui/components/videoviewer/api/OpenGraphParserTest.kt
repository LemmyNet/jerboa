package com.jerboa.ui.components.videoviewer.api

import com.jerboa.ui.components.videoviewer.OpenGraphParser
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenGraphParserTest {
    private val sendVidSamplePage = loadResourceAsString("samples/sendvid-sample.html")

    @Test
    fun `should parse head from html page`() {
        val headContent = OpenGraphParser.parseHeadFromHtml(sendVidSamplePage)

        assertTrue(headContent != null)
        assertTrue(headContent!!.startsWith("<meta charset=\"utf-8\"/>"))
    }

    @Test
    fun `should find properties fields`() {
        val props = OpenGraphParser.findAllPropertiesFromHtml(sendVidSamplePage)

        assertTrue(props.isNotEmpty())
        assertTrue(props.contains(Pair("og:video:type", "video/mp4")))
    }

    fun loadResourceAsString(resourceName: String): String {
        val classLoader = javaClass.classLoader ?: ClassLoader.getSystemClassLoader()
        return classLoader.getResourceAsStream(resourceName)?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalArgumentException("Resource $resourceName not found")
    }
}
