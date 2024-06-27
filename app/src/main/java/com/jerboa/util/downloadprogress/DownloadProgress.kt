package com.jerboa.util.downloadprogress

import com.jerboa.api.API
import kotlinx.coroutines.flow.MutableStateFlow

object DownloadProgress {
    private val initProgress = ProgressEvent("", 0, 0)

    val downloadProgressFlow = MutableStateFlow(initProgress)

    val downloadProgressHttpClient =
        API.httpClient
            .newBuilder()
            .addInterceptor(DownloadProgressInterceptor(downloadProgressFlow))
            .build()
}
