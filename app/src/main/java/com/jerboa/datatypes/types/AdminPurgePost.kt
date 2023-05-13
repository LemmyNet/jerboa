package com.jerboa.datatypes.types

data class AdminPurgePost(
    var id: Int,
    var admin_person_id: PersonId,
    var community_id: CommunityId,
    var reason: String? = null,
    var when_: String,
)