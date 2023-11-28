package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurgeComment(
    val comment_id: CommentId,
    val reason: String? = null,
) : Parcelable
