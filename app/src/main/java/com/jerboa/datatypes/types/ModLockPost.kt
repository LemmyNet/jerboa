package com.jerboa.datatypes.types

data class ModLockPost(
    val id: Int,
    val mod_person_id: PersonId,
    val post_id: PostId,
    val locked: Boolean,
    val when_: String,
)
