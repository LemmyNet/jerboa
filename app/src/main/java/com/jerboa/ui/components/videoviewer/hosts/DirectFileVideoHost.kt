package com.jerboa.ui.components.videoviewer.hosts

import com.jerboa.feat.videoExtensionFromUrl
import com.jerboa.ui.components.videoviewer.EmbeddedData

class DirectFileVideoHost : SupportedVideoHost {
    companion object {
        fun isDirectUrl(url: String): Boolean = videoExtensionFromUrl(url) != null
    }

    override fun isSupported(url: String): Boolean = isDirectUrl(url)

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
