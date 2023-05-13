package com.jerboa.datatypes.types

data class CreatePrivateMessage(
    val content: String,
    val recipient_id: PersonId,
    val auth: String,
)
