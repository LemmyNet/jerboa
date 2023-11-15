package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemovePost(
    val post_id: PostId,
    val removed: Boolean,
    val reason: String? = null,
) : Parcelable
