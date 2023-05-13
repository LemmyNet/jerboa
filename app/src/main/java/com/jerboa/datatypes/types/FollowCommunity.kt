package com.jerboa.datatypes.types

data class FollowCommunity(
    var community_id: CommunityId,
    var follow: Boolean,
    var auth: String,
)