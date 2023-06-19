package com.jerboa.datatypes.types

data class CustomEmojiKeyword(
    val id: Int,
    val custom_emoji_id: CustomEmojiId,
    val keyword: String,
)
