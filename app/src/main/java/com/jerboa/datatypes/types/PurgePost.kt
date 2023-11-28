package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurgePost(
    val post_id: PostId,
    val reason: String? = null,
) : Parcelable
