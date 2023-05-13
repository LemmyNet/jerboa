package com.jerboa.datatypes.types

data class AddModToCommunity(
    var community_id: CommunityId,
    var person_id: PersonId,
    var added: Boolean,
    var auth: String,
)