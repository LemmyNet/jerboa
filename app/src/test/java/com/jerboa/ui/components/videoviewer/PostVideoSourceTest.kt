package com.jerboa.ui.components.videoviewer

import com.jerboa.datatypes.samplePost
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostVideoSourceTest {
    @Test
    fun `direct video post url is resolvable`() {
        val post = samplePost.copy(url = "https://example.com/video.mp4", embed_video_url = null)

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.ResolvablePostUrl("https://example.com/video.mp4"), source)
    }

    @Test
    fun `hosted redgifs post url is resolvable`() {
        val post = samplePost.copy(url = "https://redgifs.com/watch/exampleid", embed_video_url = null)

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.ResolvablePostUrl("https://redgifs.com/watch/exampleid"), source)
    }

    @Test
    fun `direct embed video url is used as fallback when post url is absent`() {
        val post = samplePost.copy(url = null, embed_video_url = "https://example.com/video.mp4")

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.DirectEmbedUrl("https://example.com/video.mp4"), source)
    }

    @Test
    fun `direct embed video url is used as fallback when post url is unsupported`() {
        val post = samplePost.copy(url = "https://example.com/article", embed_video_url = "https://example.com/video.mp4")

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.DirectEmbedUrl("https://example.com/video.mp4"), source)
    }

    @Test
    fun `supported post url takes precedence over direct embed video url`() {
        val post = samplePost.copy(
            url = "https://redgifs.com/watch/exampleid",
            embed_video_url = "https://example.com/video.mp4",
        )

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.ResolvablePostUrl("https://redgifs.com/watch/exampleid"), source)
    }

    @Test
    fun `unsupported generic url with no embed yields no source`() {
        val post = samplePost.copy(url = "https://example.com/article", embed_video_url = null)

        val source = PostVideoSource.fromPost(post)

        assertNull(source)
    }

    @Test
    fun `post with no url and no embed yields no source`() {
        val post = samplePost.copy(url = null, embed_video_url = null)

        val source = PostVideoSource.fromPost(post)

        assertNull(source)
    }

    @Test
    fun `http post url is normalized to https`() {
        val post = samplePost.copy(url = "http://example.com/video.mp4", embed_video_url = null)

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.ResolvablePostUrl("https://example.com/video.mp4"), source)
    }

    @Test
    fun `http embed video url is normalized to https`() {
        val post = samplePost.copy(url = null, embed_video_url = "http://example.com/video.mp4")

        val source = PostVideoSource.fromPost(post)

        assertEquals(PostVideoSource.DirectEmbedUrl("https://example.com/video.mp4"), source)
    }
}
