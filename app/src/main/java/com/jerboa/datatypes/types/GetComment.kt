package com.jerboa.datatypes.types

data class GetComment(
    val id: CommentId,
    val auth: String? = null,
)
