package com.jerboa.datatypes.types

data class EditCustomEmoji(
    var id: CustomEmojiId,
    var category: String,
    var image_url: String,
    var alt_text: String,
    var keywords: Array<String>,
    var auth: String,
)