package com.jerboa.datatypes.types

data class DeletePost(
    val post_id: PostId,
    val deleted: Boolean,
    val auth: String,
)
