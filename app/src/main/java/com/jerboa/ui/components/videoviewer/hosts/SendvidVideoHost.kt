package com.jerboa.ui.components.videoviewer.hosts

import com.jerboa.api.API
import com.jerboa.ui.components.videoviewer.EmbeddedData
import com.jerboa.ui.components.videoviewer.OpenGraphParser
import com.jerboa.ui.components.videoviewer.ResourceMissing
import okhttp3.Request
import java.util.regex.Pattern

/**
 * VideoHost implementation for Sendvid.com
 *
 * Sendvid implements full OG, but it limits the links with 2 hours expiry
 * So this custom implementation fetches a new link each time.
 * By parsing the OG tags
 */
class SendvidVideoHost : SupportedVideoHost {
    companion object {
        private val SENDVID_PATTERN = Pattern.compile("(?:https?://)?(?:www\\.)?sendvid\\.com/([a-zA-Z0-9]+)(?:\\?.*)?")
    }

    override fun isSupported(url: String): Boolean = SENDVID_PATTERN.matcher(url).find()

    override fun getVideoData(url: String): Result<EmbeddedData> =
        runCatching {
            val request = Request
                .Builder()
                .url(url)
                .build()

            val response = API.httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                throw ResourceMissing()
            }

            val responseBody = response.body?.string() ?: throw IllegalStateException("Empty response from Sendvid")

            val tags = OpenGraphParser.Companion.findAllPropertiesFromHtml(responseBody)

            val title = OpenGraphParser.Companion.findContent(tags, OpenGraphParser.Companion.OG_TITLE)
            val thumbnailUrl = OpenGraphParser.Companion.findContent(tags, OpenGraphParser.Companion.OG_IMAGE)
            val videoUrl =
                OpenGraphParser.Companion.findContent(tags, OpenGraphParser.Companion.OG_VIDEO)
                    ?: throw IllegalArgumentException("No video source found in Sendvid page")
            val videoWidth =
                OpenGraphParser.Companion.findContentAsInt(tags, OpenGraphParser.Companion.OG_VIDEO_WITH)
                    ?: throw IllegalArgumentException("No video width found in Sendvid page")
            val videoHeight =
                OpenGraphParser.Companion.findContentAsInt(tags, OpenGraphParser.Companion.OG_VIDEO_HEIGHT)
                    ?: throw IllegalArgumentException("No video height found in Sendvid page")

            EmbeddedData(
                videoUrl = videoUrl,
                thumbnailUrl = thumbnailUrl,
                typeName = getShortTypeName(),
                title = title,
                height = videoHeight,
                width = videoWidth,
                aspectRatio = videoWidth / (videoHeight * 1F),
            )
        }

    override fun getShortTypeName(): String = "Sendvid"
}
