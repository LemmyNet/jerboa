package com.jerboa.util.downloadprogress

import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadProgressResponseBody(
    val downloadIdentifier: String,
    val responseBody: ResponseBody,
    val downloadFlow: MutableStateFlow<ProgressEvent>,
) : ResponseBody() {
    private lateinit var bufferedSource: BufferedSource

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (!this::bufferedSource.isInitialized) {
            bufferedSource = getForwardSource(responseBody.source()).buffer()
        }
        return bufferedSource
    }

    private fun getForwardSource(source: Source): Source =
        object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(
                sink: Buffer,
                byteCount: Long,
            ): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                downloadFlow.tryEmit(ProgressEvent(downloadIdentifier, responseBody.contentLength(), totalBytesRead))
                return bytesRead
            }
        }
}
