package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateComment(
    val content: String,
    val post_id: PostId,
    val parent_id: CommentId? = null,
    val language_id: LanguageId? = null,
) : Parcelable
