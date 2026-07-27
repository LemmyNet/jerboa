package com.jerboa.ui.components.imageviewer

import com.jerboa.feat.PostLinkType
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageViewerScreenTest {
    private val avifPostLink = PostLinkType.fromURL("https://example.com/image.avif")

    @Test
    fun `reports generic loading failure`() {
        assertEquals(
            ImageState.Failed,
            imageStateFromError(avifPostLink, RuntimeException("timeout")),
        )
    }

    @Test
    fun `reports decoder failure with image format`() {
        val error = IllegalStateException("Unable to decode image")

        assertEquals(ImageState.FailedDecode("avif"), imageStateFromError(avifPostLink, error))
    }
}
