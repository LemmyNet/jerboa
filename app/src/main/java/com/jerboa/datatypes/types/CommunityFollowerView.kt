package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityFollowerView(
    val community: Community,
    val follower: Person,
) : Parcelable
