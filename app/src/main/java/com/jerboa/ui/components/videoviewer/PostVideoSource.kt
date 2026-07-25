package com.jerboa.ui.components.videoviewer

import com.jerboa.toHttps
import com.jerboa.ui.components.videoviewer.hosts.DirectFileVideoHost
import it.vercruysse.lemmyapi.datatypes.Post

sealed interface PostVideoSource {
    /**
     * [Post.url] is supported by one of [VideoHostComposer]'s hosts and must be resolved
     * (possibly over the network) before it can be played.
     */
    data class ResolvablePostUrl(val url: String) : PostVideoSource

    /**
     * [Post.embed_video_url] already points directly at a playable media file, so it can be
     * played immediately without any resolution step.
     */
    data class DirectEmbedUrl(val url: String) : PostVideoSource

    companion object {
        fun fromPost(post: Post): PostVideoSource? {
            val url = post.url?.toHttps()
            val embedVideoUrl = post.embed_video_url?.toHttps()

            return when {
                // Has precedence bc example Sendvid has embedded video URL, but it will be expired
                url != null && VideoHostComposer.isVideo(url) -> ResolvablePostUrl(url)
                embedVideoUrl != null && DirectFileVideoHost.isDirectUrl(embedVideoUrl) -> DirectEmbedUrl(embedVideoUrl)
                else -> null
            }
        }
    }
}
