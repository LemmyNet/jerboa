package com.jerboa.datatypes.types

data class ModRemovePost(
    var id: Int,
    var mod_person_id: PersonId,
    var post_id: PostId,
    var reason: String? = null,
    var removed: Boolean,
    var when_: String,
)