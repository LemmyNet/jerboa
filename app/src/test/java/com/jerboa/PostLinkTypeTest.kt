package com.jerboa

import com.jerboa.feat.PostLinkType
import org.junit.Assert.assertEquals
import org.junit.Test

class PostLinkTypeTest {
    @Test
    fun `classifies direct image and video links`() {
        assertEquals(
            PostLinkType.Image(
                sourceUrl = "https://example.com/image.avif?size=large#image",
                proxiedUrl = null,
                filename = "image.avif",
                extension = "avif",
            ),
            PostLinkType.fromURL("https://example.com/image.avif?size=large#image"),
        )
        assertEquals(
            PostLinkType.Video(
                sourceUrl = "https://example.com/video.MP4?download=true",
                filename = "video.MP4",
                extension = "mp4",
            ),
            PostLinkType.fromURL("https://example.com/video.MP4?download=true"),
        )
    }

    @Test
    fun `extracts and classifies v3 proxy image URL`() {
        val proxyUrl =
            "https://lemmy.ml/api/v3/image_proxy" +
                "?url=https%3A%2F%2Flemmy.world%2Fpictrs%2Fimage%2Fimage.webp"

        assertEquals(
            PostLinkType.Image(
                sourceUrl = proxyUrl,
                proxiedUrl = "https://lemmy.world/pictrs/image/image.webp",
                filename = "image.webp",
                extension = "webp",
            ),
            PostLinkType.fromURL(proxyUrl),
        )
    }

    @Test
    fun `extracts and classifies v4 proxy image URL`() {
        val proxyUrl =
            "https://lemmy.ml/api/v4/image/proxy?size=1024" +
                "&url=https%3A%2F%2Fexample.com%2Fimage.jpeg%3Fraw%3Dtrue"

        assertEquals(
            PostLinkType.Image(
                sourceUrl = proxyUrl,
                proxiedUrl = "https://example.com/image.jpeg?raw=true",
                filename = "image.jpeg",
                extension = "jpeg",
            ),
            PostLinkType.fromURL(proxyUrl),
        )
    }

    @Test
    fun `does not inspect image URLs in unrelated query parameters`() {
        val url = "https://example.com/page?url=https%3A%2F%2Fexample.com%2Fimage.png"

        assertEquals(PostLinkType.Link(url), PostLinkType.fromURL(url))
    }

    @Test
    fun `classifies media without restricting schemes`() {
        assertEquals(
            PostLinkType.Image("test.jpg", null, "test.jpg", "jpg"),
            PostLinkType.fromURL("test.jpg"),
        )
        assertEquals(
            PostLinkType.Image("content://example.com/image.png", null, "image.png", "png"),
            PostLinkType.fromURL("content://example.com/image.png"),
        )
    }

    @Test
    fun `keeps extensionless and unsupported values as links`() {
        assertEquals(
            PostLinkType.Link("https://example.com/image"),
            PostLinkType.fromURL("https://example.com/image"),
        )
        assertEquals(
            PostLinkType.Link(
                sourceUrl = "https://example.com/image.csv",
                filename = "image.csv",
                extension = "csv",
            ),
            PostLinkType.fromURL("https://example.com/image.csv"),
        )
    }

    @Test
    fun `preserves plus characters in an encoded proxy target`() {
        val proxyUrl =
            "https://lemmy.ml/api/v3/image_proxy" +
                "?url=https%3A%2F%2Fexample.com%2Fimage%2Bedited.png"

        assertEquals(
            PostLinkType.Image(
                sourceUrl = proxyUrl,
                proxiedUrl = "https://example.com/image+edited.png",
                filename = "image+edited.png",
                extension = "png",
            ),
            PostLinkType.fromURL(proxyUrl),
        )
    }

    @Test
    fun `classifies a non-http proxy target without using it as the source`() {
        val proxyUrl =
            "https://lemmy.ml/api/v4/image/proxy" +
                "?url=content%3A%2F%2Fexample.com%2Fimage.heic"

        assertEquals(
            PostLinkType.Image(
                sourceUrl = proxyUrl,
                proxiedUrl = "content://example.com/image.heic",
                filename = "image.heic",
                extension = "heic",
            ),
            PostLinkType.fromURL(proxyUrl),
        )
    }

    @Test
    fun `keeps invalid and incomplete proxy URLs as links`() {
        val missingTarget = "https://lemmy.ml/api/v3/image_proxy?size=large"
        val invalidTarget = "https://lemmy.ml/api/v4/image/proxy?url=%"

        assertEquals(PostLinkType.Link(missingTarget), PostLinkType.fromURL(missingTarget))
        assertEquals(PostLinkType.Link(invalidTarget), PostLinkType.fromURL(invalidTarget))
    }
}
