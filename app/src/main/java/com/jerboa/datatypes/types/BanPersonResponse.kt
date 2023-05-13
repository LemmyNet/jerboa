package com.jerboa.datatypes.types

data class BanPersonResponse(
    val person_view: PersonView,
    val banned: Boolean,
)
