package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DistinguishComment(
    val comment_id: CommentId,
    val distinguished: Boolean,
) : Parcelable
