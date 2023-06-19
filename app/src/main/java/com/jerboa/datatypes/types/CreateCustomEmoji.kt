package com.jerboa.datatypes.types

data class CreateCustomEmoji(
    val category: String,
    val shortcode: String,
    val image_url: String,
    val alt_text: String,
    val keywords: List<String>,
    val auth: String,
)
