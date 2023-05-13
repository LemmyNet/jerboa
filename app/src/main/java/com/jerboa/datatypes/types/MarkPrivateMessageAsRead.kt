package com.jerboa.datatypes.types

data class MarkPrivateMessageAsRead(
    var private_message_id: PrivateMessageId,
    var read: Boolean,
    var auth: String,
)