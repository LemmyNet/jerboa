package com.jerboa.datatypes.types

data class CustomEmoji(
    var id: CustomEmojiId,
    var local_site_id: LocalSiteId,
    var shortcode: String,
    var image_url: String,
    var alt_text: String,
    var category: String,
    var published: String,
    var updated: String? = null,
)