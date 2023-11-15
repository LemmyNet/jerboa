package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class MarkPostAsRead(
    val post_id: PostId? = null,
    val post_ids: List<PostId>? = null,
    val read: Boolean,
) : Parcelable
