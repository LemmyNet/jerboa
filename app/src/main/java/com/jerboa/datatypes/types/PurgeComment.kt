package com.jerboa.datatypes.types

data class PurgeComment(
    val comment_id: CommentId,
    val reason: String? = null,
    val auth: String,
)
