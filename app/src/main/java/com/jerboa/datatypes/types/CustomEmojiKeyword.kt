package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomEmojiKeyword(
    val id: Int,
    val custom_emoji_id: CustomEmojiId,
    val keyword: String,
) : Parcelable
