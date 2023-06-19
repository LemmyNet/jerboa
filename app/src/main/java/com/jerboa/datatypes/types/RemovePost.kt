package com.jerboa.datatypes.types

data class RemovePost(
    val post_id: PostId,
    val removed: Boolean,
    val reason: String? = null,
    val auth: String,
)
