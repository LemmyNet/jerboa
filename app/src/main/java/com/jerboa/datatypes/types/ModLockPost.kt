package com.jerboa.datatypes.types

data class ModLockPost(
    var id: Int,
    var mod_person_id: PersonId,
    var post_id: PostId,
    var locked: Boolean,
    var when_: String,
)