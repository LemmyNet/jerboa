package com.jerboa.datatypes.types

data class RemoveComment(
    val comment_id: CommentId,
    val removed: Boolean,
    val reason: String? = null,
    val auth: String,
)
