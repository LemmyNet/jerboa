package com.jerboa.ui.components.videoviewer

import android.util.Log
import com.jerboa.ui.components.videoviewer.hosts.DirectFileVideoHost
import com.jerboa.ui.components.videoviewer.hosts.RedgifsVideoHost
import com.jerboa.ui.components.videoviewer.hosts.SendvidVideoHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoHostComposer {
    companion object {
        private val lruCache = object : LinkedHashMap<String, Result<EmbeddedData>?>(200, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Result<EmbeddedData>?>): Boolean {
                return size > 200 // Limit cache to 200 entries
            }
        }

        val instances = listOf(
            DirectFileVideoHost(),
            RedgifsVideoHost(),
            SendvidVideoHost(),
        )

        fun isVideo(url: String): Boolean = instances.any { it.isSupported(url) }

        suspend fun getVideoData(url: String): Result<EmbeddedData> {
            Log.d("VideoHostComposer", "Getting video data for $url")
            if (lruCache.containsKey(url)) return lruCache[url]!!

            val data = withContext(Dispatchers.IO) {
                return@withContext instances.first { it.isSupported(url) }.getVideoData(url)
            }

            lruCache[url] = data
            return data
        }

        fun getVideoDataFromCache(url: String): Result<EmbeddedData>? = lruCache[url]
    }
}
