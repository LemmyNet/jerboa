package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockCommunity(
    val community_id: CommunityId,
    val block: Boolean,
) : Parcelable
