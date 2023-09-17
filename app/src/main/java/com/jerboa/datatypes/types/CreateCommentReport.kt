package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateCommentReport(
    val comment_id: CommentId,
    val reason: String,
    val auth: String,
) : Parcelable
