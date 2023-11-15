package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurgeCommunity(
    val community_id: CommunityId,
    val reason: String? = null,
) : Parcelable
