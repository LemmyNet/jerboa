package com.jerboa.datatypes.types

data class EditPrivateMessage(
    var private_message_id: PrivateMessageId,
    var content: String,
    var auth: String,
)