package com.jerboa.datatypes.types

data class GetReportCount(
    var community_id: CommunityId? = null,
    var auth: String,
)