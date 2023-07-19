package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetReportCount(
    val community_id: CommunityId? = null,
    val auth: String,
) : Parcelable
