package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowCommunity(
    val community_id: CommunityId,
    val follow: Boolean,
    val auth: String,
) : Parcelable
