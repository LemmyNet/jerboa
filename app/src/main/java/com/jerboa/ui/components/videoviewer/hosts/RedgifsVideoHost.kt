package com.jerboa.ui.components.videoviewer.hosts

import com.jerboa.JSON
import com.jerboa.api.API
import com.jerboa.ui.components.videoviewer.EmbeddedData
import com.jerboa.ui.components.videoviewer.ResourceMissing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Request
import java.util.regex.Pattern

class RedgifsVideoHost : SupportedVideoHost {
    companion object {
        private val REDGIFS_PATTERN = Pattern.compile("(?:https?://)?(?:www\\.)?redgifs\\.com/watch/([a-zA-Z0-9]+)(?:\\?.*)?")
        private val REDGIFS_EMBED_PATTERN = Pattern.compile("(?:https?://)?(?:www\\.)?redgifs\\.com/ifr/([a-zA-Z0-9]+)(?:\\?.*)?")
        private val REDGIFS_THUMBNAIL_PATTERN = Pattern.compile("https://media\\.redgifs\\.com/([a-zA-Z0-9]+)")
    }

    @Serializable
    private data class RedGifsOEmbedResponse(
        val version: String,
        @SerialName("provider_url") val providerUrl: String,
        @SerialName("provider_name") val providerName: String,
        val type: String,
        val title: String,
        val html: String,
        val width: Int,
        val height: Int,
        @SerialName("thumbnail_url") val thumbnailUrl: String,
        @SerialName("thumbnail_width") val thumbnailWidth: Int?,
        @SerialName("thumbnail_height") val thumbnailHeight: Int?,
    )

    override fun isSupported(url: String): Boolean = REDGIFS_PATTERN.matcher(url).matches() || REDGIFS_EMBED_PATTERN.matcher(url).matches()

    override fun getVideoData(url: String): Result<EmbeddedData> =
        runCatching {
            val id = extractRedgifsId(url) ?: throw IllegalArgumentException("Invalid Redgifs URL")
            val apiUrl = "https://api.redgifs.com/v1/oembed?url=https://redgifs.com/watch/$id"

            val request = Request
                .Builder()
                .url(apiUrl)
                .build()

            val response = API.httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                throw ResourceMissing()
            }

            val stringResponseBody = response.body?.string() ?: throw IllegalStateException("Empty response from Redgifs API")

            val responseBody = JSON.decodeFromString<RedGifsOEmbedResponse>(stringResponseBody)
            val videoId = extractThumbnailId(responseBody.thumbnailUrl) ?: throw IllegalStateException("No video ID found in thumbnail URL")

            EmbeddedData(
                videoUrl = "https://media.redgifs.com/$videoId.mp4",
                thumbnailUrl = responseBody.thumbnailUrl,
                title = responseBody.title,
                width = responseBody.width,
                height = responseBody.height,
                aspectRatio = if (responseBody.width > 0 &&
                    responseBody.height > 0
                ) {
                    responseBody.width.toFloat() / responseBody.height.toFloat()
                } else {
                    16F / 9F
                },
                typeName = getShortTypeName(),
            )
        }

    private fun extractRedgifsId(url: String): String? {
        val matcher = REDGIFS_PATTERN.matcher(url)
        if (matcher.find()) {
            return matcher.group(1)
        }

        val embedMatcher = REDGIFS_EMBED_PATTERN.matcher(url)
        if (embedMatcher.find()) {
            return embedMatcher.group(1)
        }

        return null
    }

    // Id is case sensitive but url isn't thus must retrieve original capitalization
    private fun extractThumbnailId(url: String): String? {
        val matcher = REDGIFS_THUMBNAIL_PATTERN.matcher(url)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    override fun getShortTypeName(): String = "Redgifs"
}
