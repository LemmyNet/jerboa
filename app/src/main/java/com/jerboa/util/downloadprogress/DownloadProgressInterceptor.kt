package com.jerboa.util.downloadprogress

import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DownloadProgressInterceptor(
    private val downloadFlow: MutableStateFlow<ProgressEvent>,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val responseBuilder = originalResponse.newBuilder()

        val downloadIdentifier = originalResponse.request.url.toString()

        val downloadResponseBody = originalResponse.body?.let {
            DownloadProgressResponseBody(
                downloadIdentifier,
                it,
                downloadFlow,
            )
        }

        responseBuilder.body(downloadResponseBody)
        return responseBuilder.build()
    }
}
