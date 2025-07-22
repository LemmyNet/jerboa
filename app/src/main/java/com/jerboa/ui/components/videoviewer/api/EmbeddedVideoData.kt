package com.jerboa.ui.components.videoviewer.api

data class EmbeddedVideoData(
    val url: String,
    val thumbnailUrl: String,
    val typeName: String,
    val title: String?,
    val description: String?,
    val height: Int,
    val width: Int,
    val aspectRatio: Float,
)
