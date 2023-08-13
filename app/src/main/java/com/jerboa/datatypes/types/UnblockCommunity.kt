package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnblockCommunity(
    val community_id: CommunityId,
    val block: Boolean,
    val auth: String,
) : Parcelable
