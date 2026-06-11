package com.jerboa.ui.components.videoviewer

import java.util.regex.Pattern

/**
 * Little custom class for parsing basic HTML pages for OGP tags
 *
 * Couldn't find any existing Java/Kotlin libs and
 * didnt want to introduce whole XML parsing libs like Jsoup
 * So I ended up with this bit of regex magic.
 */
class OpenGraphParser {
    companion object {
        private val HEAD_PATTERN = Pattern.compile("<head>(.*?)</head>", Pattern.DOTALL).toRegex()
        private val META_TAGS_PATTERN = Pattern.compile("<meta (.*?)/?>").toRegex()
        private val PROPERTY_ATTR_PATTERN = Pattern.compile("property=\"(.*?)\"").toRegex()
        private val CONTENT_ATTR_PATTERN = Pattern.compile("content=\"(.*?)\"").toRegex()

        val OG_IMAGE = "og:image"
        val OG_TITLE = "og:title"
        val OG_DESCRIPTION = "og:description"
        val OG_URL = "og:url"
        val OG_TYPE = "og:type"
        val OG_VIDEO = "og:video"
        val OG_VIDEO_WITH = "og:video:width"
        val OG_VIDEO_HEIGHT = "og:video:height"

        fun parseHeadFromHtml(html: String): String? {
            val headMatcher = HEAD_PATTERN.find(html) ?: return null
            return headMatcher.groupValues[1]
        }

        fun parseMetaTagsFromHtml(html: String): List<String> =
            META_TAGS_PATTERN
                .findAll(html)
                .map { it.groupValues[1] }
                .toList()

        fun findAllPropertyFields(fields: List<String>): List<Pair<String, String>> =
            fields
                .map {
                    Pair(
                        PROPERTY_ATTR_PATTERN.find(it)?.groupValues?.get(1),
                        CONTENT_ATTR_PATTERN.find(it)?.groupValues?.get(1),
                    )
                }.filter { it.first != null && it.second != null }
                .map { Pair(it.first!!, it.second!!) }
                .toList()

        fun findAllPropertiesFromHtml(html: String): List<Pair<String, String>> {
            val head = parseHeadFromHtml(html) ?: return emptyList()
            return findAllPropertyFields(parseMetaTagsFromHtml(head))
        }

        fun findContent(
            tags: List<Pair<String, String>>,
            key: String,
        ): String? = tags.find { it.first == key }?.second

        fun findContentAsInt(
            tags: List<Pair<String, String>>,
            key: String,
        ): Int? = findContent(tags, key)?.toIntOrNull()
    }
}
