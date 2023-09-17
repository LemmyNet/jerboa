package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateCommentLike(
    val comment_id: CommentId,
    val score: Int,
    val auth: String,
) : Parcelable
