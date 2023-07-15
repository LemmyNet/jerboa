package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkPostAsRead(
    val post_id: PostId,
    val read: Boolean,
    val auth: String,
) : Parcelable
