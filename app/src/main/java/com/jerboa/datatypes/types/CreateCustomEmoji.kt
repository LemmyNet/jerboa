package com.jerboa.datatypes.types

data class CreateCustomEmoji(
    var category: String,
    var shortcode: String,
    var image_url: String,
    var alt_text: String,
    var keywords: Array<String>,
    var auth: String,
)