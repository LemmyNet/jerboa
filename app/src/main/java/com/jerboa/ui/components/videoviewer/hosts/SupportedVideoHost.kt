package com.jerboa.ui.components.videoviewer.hosts

import com.jerboa.ui.components.videoviewer.EmbeddedData

sealed interface SupportedVideoHost {
    fun isSupported(url: String): Boolean

    fun getVideoData(url: String): Result<EmbeddedData>

    fun getShortTypeName(): String
}
