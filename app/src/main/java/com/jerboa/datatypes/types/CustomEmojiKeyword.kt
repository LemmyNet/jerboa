package com.jerboa.datatypes.types

data class CustomEmojiKeyword(
    var id: Int,
    var custom_emoji_id: CustomEmojiId,
    var keyword: String,
)