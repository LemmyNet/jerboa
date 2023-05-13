package com.jerboa.datatypes.types

data class ModHideCommunity(
    var id: Int,
    var community_id: CommunityId,
    var mod_person_id: PersonId,
    var when_: String,
    var reason: String? = null,
    var hidden: Boolean,
)