package com.jerboa.datatypes.types

data class LockPost(
    val post_id: PostId,
    val locked: Boolean,
    val auth: String,
)
