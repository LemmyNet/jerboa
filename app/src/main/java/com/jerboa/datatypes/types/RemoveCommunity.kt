package com.jerboa.datatypes.types

data class RemoveCommunity(
    val community_id: CommunityId,
    val removed: Boolean,
    val reason: String? = null,
    val expires: Int? = null,
    val auth: String,
)
