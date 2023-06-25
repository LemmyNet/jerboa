package com.jerboa.datatypes.types

data class AddAdmin(
    val person_id: PersonId,
    val added: Boolean,
    val auth: String,
)
