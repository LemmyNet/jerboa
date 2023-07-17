package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class CommentResponse(
    val comment_view: CommentView,
    val recipient_ids: List<LocalUserId>,
    val form_id: String? = null,
) : Parcelable
