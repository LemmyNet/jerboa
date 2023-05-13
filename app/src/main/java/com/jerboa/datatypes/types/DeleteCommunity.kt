package com.jerboa.datatypes.types

data class DeleteCommunity(
    var community_id: CommunityId,
    var deleted: Boolean,
    var auth: String,
)