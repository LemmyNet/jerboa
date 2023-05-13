package com.jerboa.datatypes.types

data class BlockPersonResponse(
    val person_view: PersonView,
    val blocked: Boolean,
)
