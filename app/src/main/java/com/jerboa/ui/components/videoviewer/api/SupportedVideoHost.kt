package com.jerboa.ui.components.videoviewer.api

sealed interface SupportedVideoHost {

    fun isSupported(url: String): Boolean
    fun getVideoData(url: String): Result<EmbeddedVideoData>
    fun getShortTypeName(): String
}