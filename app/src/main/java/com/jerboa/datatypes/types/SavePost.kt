package com.jerboa.datatypes.types

data class SavePost(
    val post_id: PostId,
    val save: Boolean,
    val auth: String,
)
