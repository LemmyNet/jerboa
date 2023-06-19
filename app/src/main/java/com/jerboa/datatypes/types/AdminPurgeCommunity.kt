package com.jerboa.datatypes.types

data class AdminPurgeCommunity(
    val id: Int,
    val admin_person_id: PersonId,
    val reason: String? = null,
    val when_: String,
)
