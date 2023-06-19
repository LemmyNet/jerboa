package com.jerboa.datatypes.types

data class GetReportCountResponse(
    val community_id: CommunityId? = null,
    val comment_reports: Int,
    val post_reports: Int,
    val private_message_reports: Int? = null,
)
