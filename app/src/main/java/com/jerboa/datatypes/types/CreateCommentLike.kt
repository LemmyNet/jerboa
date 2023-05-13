package com.jerboa.datatypes.types

data class CreateCommentLike(
    val comment_id: CommentId,
    val score: Int,
    val auth: String,
)
