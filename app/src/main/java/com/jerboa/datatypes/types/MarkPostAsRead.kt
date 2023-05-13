package com.jerboa.datatypes.types

data class MarkPostAsRead(
    var post_id: PostId,
    var read: Boolean,
    var auth: String,
)