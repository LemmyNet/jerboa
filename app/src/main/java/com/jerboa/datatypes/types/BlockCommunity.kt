package com.jerboa.datatypes.types

data class BlockCommunity(
    val community_id: CommunityId,
    val block: Boolean,
    val auth: String,
)
