package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferCommunity(
    val community_id: CommunityId,
    val person_id: PersonId,
    val auth: String,
) : Parcelable
