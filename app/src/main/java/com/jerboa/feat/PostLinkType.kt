package com.jerboa.feat

import io.ktor.http.Url

private const val V3_IMAGE_PROXY_PATH = "/api/v3/image_proxy"
private const val V4_IMAGE_PROXY_PATH = "/api/v4/image/proxy"

private val imageExtensions =
    setOf("jpg", "jpeg", "gif", "png", "svg", "webp", "avif", "bmp", "heif", "heic")
private val videoExtensions =
    setOf("mp4", "mp3", "ogg", "flv", "m4a", "3gp", "mkv", "mpeg", "mov", "webm")

/**
 * For a given post, what sort of content Jerboa treats it as.
 */
sealed interface PostLinkType {
    val sourceUrl: String

    /**
     * A link to an external website. Opens the browser.
     */
    data class Link(
        override val sourceUrl: String,
        val filename: String? = null,
        val extension: String? = null,
    ) : PostLinkType

    /**
     * An image. Opens the built-in image viewer.
     */
    data class Image(
        override val sourceUrl: String,
        /**
         * The URL embedded in an image proxy request.
         *
         * This is metadata only. Never perform I/O with this URL: use [sourceUrl] so the request
         * continues to go through the proxy endpoint.
         */
        val proxiedUrl: String?,
        val filename: String,
        val extension: String,
    ) : PostLinkType

    /**
     * A video. Should open the built-in video viewer. Also matches audio only.
     */
    data class Video(
        override val sourceUrl: String,
        val filename: String,
        val extension: String,
    ) : PostLinkType

    companion object {
        fun fromURL(url: String): PostLinkType {
            val proxiedUrl = imageProxyTarget(url)
            val metadata = mediaFileMetadata(proxiedUrl ?: url) ?: return Link(url)

            return when (metadata.extension) {
                in videoExtensions -> Video(url, metadata.filename, metadata.extension)
                in imageExtensions -> Image(url, proxiedUrl, metadata.filename, metadata.extension)
                else -> Link(url, metadata.filename, metadata.extension)
            }
        }
    }
}

private data class MediaFileMetadata(
    val filename: String,
    val extension: String,
)

private fun mediaFileMetadata(url: String): MediaFileMetadata? {
    val parsedUrl = parseUrlSafe(url) ?: return null
    val filename = parsedUrl.segments.lastOrNull() ?: return null
    val extension = filename
        .substringAfterLast('.', "")
        .takeIf { it.isNotBlank() }
        ?.lowercase()
        ?: return null

    return MediaFileMetadata(filename, extension)
}

fun extensionFromUrl(url: String): String? = mediaFileMetadata(url)?.extension

fun videoExtensionFromUrl(url: String): String? = extensionFromUrl(url)?.takeIf { it in videoExtensions }

private fun imageProxyTarget(url: String): String? {
    val proxyUrl = parseUrlSafe(url) ?: return null
    if (!proxyUrl.encodedPath.isImageProxyPath()) return null

    return proxyUrl.parameters["url"]?.takeIf { it.isNotBlank() }
}

private fun String.isImageProxyPath(): Boolean =
    this == V3_IMAGE_PROXY_PATH ||
        endsWith(V3_IMAGE_PROXY_PATH) ||
        this == V4_IMAGE_PROXY_PATH ||
        endsWith(V4_IMAGE_PROXY_PATH)

private fun parseUrlSafe(url: String): Url? = runCatching { Url(url) }.getOrNull()
