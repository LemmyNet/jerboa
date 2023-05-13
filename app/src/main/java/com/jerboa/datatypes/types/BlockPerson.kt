package com.jerboa.datatypes.types

data class BlockPerson(
    var person_id: PersonId,
    var block: Boolean,
    var auth: String,
)