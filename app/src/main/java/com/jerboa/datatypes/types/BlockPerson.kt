package com.jerboa.datatypes.types

data class BlockPerson(
    val person_id: PersonId,
    val block: Boolean,
    val auth: String,
)
