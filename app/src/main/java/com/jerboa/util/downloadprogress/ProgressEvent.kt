package com.jerboa.util.downloadprogress

data class ProgressEvent(
    val progress: Float,
    val contentLength: Long,
    val downloadURL: String,
    val bytesRead: Long,
    val percentIsAvailable: Boolean,
) {

    constructor(downloadIdentifier: String, contentLength: Long, bytesRead: Long) :
        this(
            progress = (bytesRead.toFloat() / contentLength),
            contentLength = contentLength,
            downloadURL = downloadIdentifier,
            bytesRead = bytesRead,
            percentIsAvailable = contentLength > 0,
        )
}
