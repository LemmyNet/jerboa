package com.jerboa.datatypes.types

data class CreatePrivateMessage(
    var content: String,
    var recipient_id: PersonId,
    var auth: String,
)