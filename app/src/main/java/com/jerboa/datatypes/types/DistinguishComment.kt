package com.jerboa.datatypes.types

data class DistinguishComment(
    val comment_id: CommentId,
    val distinguished: Boolean,
    val auth: String,
)
