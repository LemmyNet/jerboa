package com.jerboa.datatypes.types

data class TransferCommunity(
    var community_id: CommunityId,
    var person_id: PersonId,
    var auth: String,
)