package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeletePost(
    val post_id: PostId,
    val deleted: Boolean,
    val auth: String,
) : Parcelable
