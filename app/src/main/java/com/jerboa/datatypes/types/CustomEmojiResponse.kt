package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomEmojiResponse(
    val custom_emoji: CustomEmojiView,
) : Parcelable
