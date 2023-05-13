package com.jerboa.datatypes.types

data class DeletePost(
    var post_id: PostId,
    var deleted: Boolean,
    var auth: String,
)