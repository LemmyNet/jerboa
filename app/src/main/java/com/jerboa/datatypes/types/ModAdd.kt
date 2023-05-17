package com.jerboa.datatypes.types

data class ModAdd(
    val id: Int,
    val mod_person_id: PersonId,
    val other_person_id: PersonId,
    val removed: Boolean,
    val when_: String,
)
