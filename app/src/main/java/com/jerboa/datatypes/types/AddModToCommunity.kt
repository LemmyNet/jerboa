package com.jerboa.datatypes.types

data class AddModToCommunity(
    val community_id: CommunityId,
    val person_id: PersonId,
    val added: Boolean,
    val auth: String,
)
