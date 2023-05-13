package com.jerboa.datatypes.types

data class HideCommunity(
    var community_id: CommunityId,
    var hidden: Boolean,
    var reason: String? = null,
    var auth: String,
)