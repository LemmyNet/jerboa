package com.jerboa.datatypes.types

data class BanFromCommunity(
    var community_id: CommunityId,
    var person_id: PersonId,
    var ban: Boolean,
    var remove_data: Boolean? = null,
    var reason: String? = null,
    var expires: Int? = null,
    var auth: String,
)