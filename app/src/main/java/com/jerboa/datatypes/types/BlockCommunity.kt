package com.jerboa.datatypes.types

data class BlockCommunity(
    var community_id: CommunityId,
    var block: Boolean,
    var auth: String,
)