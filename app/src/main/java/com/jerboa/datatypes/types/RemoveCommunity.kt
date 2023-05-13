package com.jerboa.datatypes.types

data class RemoveCommunity(
    var community_id: CommunityId,
    var removed: Boolean,
    var reason: String? = null,
    var expires: Int? = null,
    var auth: String,
)