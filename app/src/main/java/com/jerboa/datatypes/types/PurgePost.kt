package com.jerboa.datatypes.types

data class PurgePost(
    var post_id: PostId,
    var reason: String? = null,
    var auth: String,
)