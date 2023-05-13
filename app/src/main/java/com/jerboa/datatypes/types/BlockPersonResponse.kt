package com.jerboa.datatypes.types

data class BlockPersonResponse(
    var person_view: PersonView,
    var blocked: Boolean,
)