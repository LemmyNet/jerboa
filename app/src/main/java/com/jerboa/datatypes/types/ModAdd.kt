package com.jerboa.datatypes.types

data class ModAdd(
    var id: Int,
    var mod_person_id: PersonId,
    var other_person_id: PersonId,
    var removed: Boolean,
    var when_: String,
)