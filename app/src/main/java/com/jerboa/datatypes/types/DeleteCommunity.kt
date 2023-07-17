package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteCommunity(
    val community_id: CommunityId,
    val deleted: Boolean,
    val auth: String,
) : Parcelable
