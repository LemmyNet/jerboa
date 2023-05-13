package com.jerboa.datatypes.types

data class AdminPurgeCommunity(
    var id: Int,
    var admin_person_id: PersonId,
    var reason: String? = null,
    var when_: String,
)