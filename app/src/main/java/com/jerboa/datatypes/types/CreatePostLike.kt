package com.jerboa.datatypes.types

data class CreatePostLike(
    val post_id: PostId,
    val score: Int,
    val auth: String,
)
