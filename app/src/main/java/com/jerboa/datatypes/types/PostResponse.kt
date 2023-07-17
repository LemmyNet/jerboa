package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostResponse(
    val post_view: PostView,
) : Parcelable
