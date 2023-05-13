package com.jerboa.datatypes.types

data class BlockCommunityResponse(
    var community_view: CommunityView,
    var blocked: Boolean,
)