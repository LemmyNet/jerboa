package com.jerboa.datatypes.types

data class GetReportCountResponse(
    var community_id: CommunityId? = null,
    var comment_reports: Int,
    var post_reports: Int,
    var private_message_reports: Int? = null,
)