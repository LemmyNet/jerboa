package com.jerboa.datatypes.types

data class MarkPersonMentionAsRead(
    var person_mention_id: PersonMentionId,
    var read: Boolean,
    var auth: String,
)