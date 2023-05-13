package com.jerboa.datatypes.types

data class ModAddCommunity(
    var id: Int,
    var mod_person_id: PersonId,
    var other_person_id: PersonId,
    var community_id: CommunityId,
    var removed: Boolean,
    var when_: String,
)