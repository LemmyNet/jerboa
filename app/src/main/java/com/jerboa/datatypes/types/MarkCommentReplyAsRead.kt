package com.jerboa.datatypes.types

data class MarkCommentReplyAsRead(
    var comment_reply_id: CommentReplyId,
    var read: Boolean,
    var auth: String,
)