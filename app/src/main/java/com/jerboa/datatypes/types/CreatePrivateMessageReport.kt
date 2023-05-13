package com.jerboa.datatypes.types

data class CreatePrivateMessageReport(
    var private_message_id: PrivateMessageId,
    var reason: String,
    var auth: String,
)