package com.jerboa.ui.components.videoviewer.api

class DirectFileVideoHost : SupportedVideoHost {
    companion object {
        private val videoRgx =
            Regex(
                pattern = "(http)?s?:?(//[^\"']*\\.(?:mp4|mp3|ogg|flv|m4a|3gp|mkv|mpeg|mov|webm))",
            )
    }

    override fun isSupported(url: String): Boolean = videoRgx.matches(url)

    override fun getVideoData(url: String): Result<EmbeddedVideoData> {
        return Result.success(
            EmbeddedVideoData(
                url = url,
                thumbnailUrl = "",
                typeName = getShortTypeName(),
                title = null,
                description = null,
                height = 0,
                width = 0,
                aspectRatio = 0f,
            )
        )
    }

    override fun getShortTypeName() = "Video"
}