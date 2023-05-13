package com.jerboa.datatypes.types

data class DeletePrivateMessage(
    var private_message_id: PrivateMessageId,
    var deleted: Boolean,
    var auth: String,
)