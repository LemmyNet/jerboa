package com.jerboa.datatypes.types

data class MarkPostAsRead(
    val post_id: PostId,
    val read: Boolean,
    val auth: String,
)
