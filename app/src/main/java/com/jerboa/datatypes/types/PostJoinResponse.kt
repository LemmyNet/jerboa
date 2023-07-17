package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostJoinResponse(
    val joined: Boolean,
) : Parcelable
