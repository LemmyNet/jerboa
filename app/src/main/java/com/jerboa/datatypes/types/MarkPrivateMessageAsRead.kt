package com.jerboa.datatypes.types

data class MarkPrivateMessageAsRead(
    val private_message_id: PrivateMessageId,
    val read: Boolean,
    val auth: String,
)
