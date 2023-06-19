package com.jerboa.datatypes.types

data class GetReportCount(
    val community_id: CommunityId? = null,
    val auth: String,
)
