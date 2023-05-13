package com.jerboa.datatypes.types

data class BanPersonResponse(
    var person_view: PersonView,
    var banned: Boolean,
)