package com.jerboa.datatypes.types

data class PurgeCommunity(
    var community_id: CommunityId,
    var reason: String? = null,
    var auth: String,
)