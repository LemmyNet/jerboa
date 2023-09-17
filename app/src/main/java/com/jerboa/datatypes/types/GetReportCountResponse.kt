package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetReportCountResponse(
    val community_id: CommunityId? = null,
    val comment_reports: Int,
    val post_reports: Int,
    val private_message_reports: Int? = null,
) : Parcelable
