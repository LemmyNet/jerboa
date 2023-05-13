package com.jerboa.datatypes.types

data class BanFromCommunityResponse(
    var person_view: PersonView,
    var banned: Boolean,
)