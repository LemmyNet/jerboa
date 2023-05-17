package com.jerboa.datatypes.types

data class MarkCommentReplyAsRead(
    val comment_reply_id: CommentReplyId,
    val read: Boolean,
    val auth: String,
)
