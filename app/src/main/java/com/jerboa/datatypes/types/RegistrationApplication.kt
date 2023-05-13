package com.jerboa.datatypes.types

data class RegistrationApplication(
    var id: Int,
    var local_user_id: LocalUserId,
    var answer: String,
    var admin_id: PersonId? = null,
    var deny_reason: String? = null,
    var published: String,
)