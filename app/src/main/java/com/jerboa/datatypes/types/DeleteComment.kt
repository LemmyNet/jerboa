package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteComment(
    val comment_id: CommentId,
    val deleted: Boolean,
    val auth: String,
) : Parcelable
