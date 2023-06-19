package com.jerboa.datatypes.types

data class AdminPurgePost(
    val id: Int,
    val admin_person_id: PersonId,
    val community_id: CommunityId,
    val reason: String? = null,
    val when_: String,
)
