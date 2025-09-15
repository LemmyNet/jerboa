package com.jerboa.ui.components.videoviewer.hosts

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectFileVideoHostTest {
    @Test
    fun testIsDirectUrl() {
        val validUrls = listOf(
            "http://example.com/video.mp4",
            "https://example.com/video.mp4",
            "http://example.com/video.mkv",
            "https://example.com/video.mkv",
            "http://example.com/path/to/video.mp4",
            "https://example.com/path/to/video.mp4",
            "https://example.com/path/to/video.mp4?query=123",
        )

        val invalidUrls = listOf(
            "http://example.com/video.avi",
            "https://example.com/video.movx",
            "http://example.com/path/to/video.mp4x",
            "https://example.com/path/to/video.mp3x",
            "http://example.com/video",
            "https://example.com/video.",
            "not a url",
            "",
            null,
            "https://www.move.org",
            "https://www.move.org/",
            "https://www.move.org/index.html",
            "https://www.moveslowlybuildbridges.com/index.html",
        )

        validUrls.forEach { url ->
            Assert.assertTrue("Expected valid for URL: $url", DirectFileVideoHost.isDirectUrl(url))
        }

        invalidUrls.forEach { url ->
            Assert.assertFalse("Expected invalid for URL: $url", DirectFileVideoHost.isDirectUrl(url))
        }
    }
}
