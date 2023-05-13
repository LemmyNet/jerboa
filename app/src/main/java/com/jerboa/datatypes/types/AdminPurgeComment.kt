package com.jerboa.datatypes.types

data class AdminPurgeComment(
    var id: Int,
    var admin_person_id: PersonId,
    var post_id: PostId,
    var reason: String? = null,
    var when_: String,
)