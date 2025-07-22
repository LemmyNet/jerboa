package com.jerboa.ui.components.videoviewer.api

class VideoHostComposer {
    companion object {
        val instances = listOf(DirectFileVideoHost())

        fun isVideo(url: String): Boolean = instances.any { it.isSupported(url) }
        fun getVideoData(url: String): Result<EmbeddedVideoData> = instances.first { it.isSupported(url) }.getVideoData(url)
    }
}