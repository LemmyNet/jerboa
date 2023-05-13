package com.jerboa.datatypes.types

data class AdminPurgeComment(
    val id: Int,
    val admin_person_id: PersonId,
    val post_id: PostId,
    val reason: String? = null,
    val when_: String,
)
