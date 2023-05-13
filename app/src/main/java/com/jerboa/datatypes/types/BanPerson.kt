package com.jerboa.datatypes.types

data class BanPerson(
    var person_id: PersonId,
    var ban: Boolean,
    var remove_data: Boolean? = null,
    var reason: String? = null,
    var expires: Int? = null,
    var auth: String,
)