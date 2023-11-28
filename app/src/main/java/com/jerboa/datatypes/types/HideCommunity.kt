package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HideCommunity(
    val community_id: CommunityId,
    val hidden: Boolean,
    val reason: String? = null,
) : Parcelable
