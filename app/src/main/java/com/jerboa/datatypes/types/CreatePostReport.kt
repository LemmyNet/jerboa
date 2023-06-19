package com.jerboa.datatypes.types

data class CreatePostReport(
    val post_id: PostId,
    val reason: String,
    val auth: String,
)
