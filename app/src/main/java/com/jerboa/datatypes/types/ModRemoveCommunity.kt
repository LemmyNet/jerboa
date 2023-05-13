package com.jerboa.datatypes.types

data class ModRemoveCommunity(
    var id: Int,
    var mod_person_id: PersonId,
    var community_id: CommunityId,
    var reason: String? = null,
    var removed: Boolean,
    var expires: String? = null,
    var when_: String,
)