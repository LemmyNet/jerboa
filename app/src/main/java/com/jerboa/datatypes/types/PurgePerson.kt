package com.jerboa.datatypes.types

data class PurgePerson(
    var person_id: PersonId,
    var reason: String? = null,
    var auth: String,
)