package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetComment(
    val id: CommentId,
    val auth: String? = null,
) : Parcelable
