package com.jerboa.datatypes.types

data class CreateCommentReport(
    val comment_id: CommentId,
    val reason: String,
    val auth: String,
)
