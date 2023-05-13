package com.jerboa.datatypes.types

data class RemovePost(
    var post_id: PostId,
    var removed: Boolean,
    var reason: String? = null,
    var auth: String,
)