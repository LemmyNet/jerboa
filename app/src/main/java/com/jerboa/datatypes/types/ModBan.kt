package com.jerboa.datatypes.types

data class ModBan(
    var id: Int,
    var mod_person_id: PersonId,
    var other_person_id: PersonId,
    var reason: String? = null,
    var banned: Boolean,
    var expires: String? = null,
    var when_: String,
)