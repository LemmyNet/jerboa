package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentReplyResponse(
    val comment_reply_view: CommentReplyView,
) : Parcelable
