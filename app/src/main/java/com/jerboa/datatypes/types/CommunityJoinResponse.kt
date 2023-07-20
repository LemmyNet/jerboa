package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityJoinResponse(
    val joined: Boolean,
) : Parcelable
