package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteCustomEmojiResponse(
    val id: CustomEmojiId,
    val success: Boolean,
) : Parcelable
