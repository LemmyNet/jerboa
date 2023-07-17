package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SaveComment(
    val comment_id: CommentId,
    val save: Boolean,
    val auth: String,
) : Parcelable
