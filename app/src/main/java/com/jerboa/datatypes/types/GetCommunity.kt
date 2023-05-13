package com.jerboa.datatypes.types

data class GetCommunity(
    val id: CommunityId? = null,
    val name: String? = null,
    val auth: String? = null,
)
