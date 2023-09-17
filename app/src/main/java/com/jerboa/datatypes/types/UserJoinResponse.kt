package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserJoinResponse(
    val joined: Boolean,
) : Parcelable
