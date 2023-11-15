package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavePost(
    val post_id: PostId,
    val save: Boolean,
) : Parcelable
