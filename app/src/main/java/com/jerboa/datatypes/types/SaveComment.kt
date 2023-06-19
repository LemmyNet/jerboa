package com.jerboa.datatypes.types

data class SaveComment(
    val comment_id: CommentId,
    val save: Boolean,
    val auth: String,
)
