package com.jerboa.datatypes.types

data class BlockCommunityResponse(
    val community_view: CommunityView,
    val blocked: Boolean,
)
