package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostJoin(
    val post_id: PostId,
) : Parcelable
