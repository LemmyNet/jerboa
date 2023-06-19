package com.jerboa.datatypes.types

data class PurgePost(
    val post_id: PostId,
    val reason: String? = null,
    val auth: String,
)
