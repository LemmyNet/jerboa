package com.jerboa.datatypes.types

data class AddAdmin(
    var person_id: PersonId,
    var added: Boolean,
    var auth: String,
)