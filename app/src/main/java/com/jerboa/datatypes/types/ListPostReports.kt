package com.jerboa.datatypes.types

data class ListPostReports(
    var page: Int? = null,
    var limit: Int? = null,
    var unresolved_only: Boolean? = null,
    var community_id: CommunityId? = null,
    var auth: String,
)