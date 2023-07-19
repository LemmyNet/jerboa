package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityBlockView(
    val person: Person,
    val community: Community,
) : Parcelable
