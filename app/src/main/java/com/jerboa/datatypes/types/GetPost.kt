package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPost(
    val id: PostId? = null,
    val comment_id: CommentId? = null,
    val auth: String? = null,
) : Parcelable
