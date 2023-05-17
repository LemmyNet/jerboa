package com.jerboa.datatypes.types

data class MarkPersonMentionAsRead(
    val person_mention_id: PersonMentionId,
    val read: Boolean,
    val auth: String,
)
