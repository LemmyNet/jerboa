package com.jerboa.datatypes.types

data class BanFromCommunityResponse(
    val person_view: PersonView,
    val banned: Boolean,
)
