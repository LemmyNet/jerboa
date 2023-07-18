package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePostLike(
    val post_id: PostId,
    val score: Int,
    val auth: String,
) : Parcelable
