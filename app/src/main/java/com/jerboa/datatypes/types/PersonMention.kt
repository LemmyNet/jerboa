package com.jerboa.datatypes.types

data class PersonMention(
    var id: PersonMentionId,
    var recipient_id: PersonId,
    var comment_id: CommentId,
    var read: Boolean,
    var published: String,
)