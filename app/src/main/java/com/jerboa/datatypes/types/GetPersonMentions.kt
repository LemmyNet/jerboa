package com.jerboa.datatypes.types

import android.os.Parcelable
import com.jerboa.datatypes.CommentSortType
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPersonMentions(
    val sort: CommentSortType? /* "Hot" | "Top" | "New" | "Old" | "Controversial" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val unread_only: Boolean? = null,
) : Parcelable
