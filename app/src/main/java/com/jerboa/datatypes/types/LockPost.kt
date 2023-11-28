package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LockPost(
    val post_id: PostId,
    val locked: Boolean,
) : Parcelable
