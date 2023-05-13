package com.jerboa.datatypes.types

data class CommentReply(
    var id: CommentReplyId,
    var recipient_id: PersonId,
    var comment_id: CommentId,
    var read: Boolean,
    var published: String,
)