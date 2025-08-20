package com.jerboa.ui.components.videoviewer.hosts

import com.jerboa.ui.components.videoviewer.EmbeddedData

class DirectFileVideoHost : SupportedVideoHost {
    companion object {
        private val videoRgx =
            Regex(
                pattern = "(http)?s?:?(//[^\"']*\\.(?:mp4|mp3|ogg|flv|m4a|3gp|mkv|mpeg|mov|webm))",
            )

        fun isDirectUrl(url: String?): Boolean {
            if (url == null) return false
            return videoRgx.containsMatchIn(url)
        }
    }

    override fun isSupported(url: String): Boolean = videoRgx.containsMatchIn(url)

    override fun getVideoData(url: String): Result<EmbeddedData> =
        Result.success(
            EmbeddedData(
                videoUrl = url,
                thumbnailUrl = null,
                typeName = getShortTypeName(),
                title = null,
                height = null,
                width = null,
                aspectRatio = 16f / 9f,
            ),
        )

    override fun getShortTypeName() = "Video"
}
