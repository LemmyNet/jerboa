package com.jerboa.datatypes.types

data class TransferCommunity(
    val community_id: CommunityId,
    val person_id: PersonId,
    val auth: String,
)
