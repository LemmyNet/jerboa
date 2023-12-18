package com.jerboa.util.downloadprogress

data class ProgressEvent(
    val progress: Float,
    val contentLength: Long,
    val downloadURL: String,
    val bytesRead: Long,
    val progressAvailable: Boolean,
) {
    constructor(downloadIdentifier: String, contentLength: Long, bytesRead: Long) :
        this(
            progress = (bytesRead.toFloat() / contentLength),
            contentLength = contentLength,
            downloadURL = downloadIdentifier,
            bytesRead = bytesRead,
            progressAvailable = contentLength > 0,
        )
}
