package com.jerboa.datatypes.types

data class DeleteCommunity(
    val community_id: CommunityId,
    val deleted: Boolean,
    val auth: String,
)
