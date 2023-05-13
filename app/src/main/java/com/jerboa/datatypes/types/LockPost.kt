package com.jerboa.datatypes.types

data class LockPost(
    var post_id: PostId,
    var locked: Boolean,
    var auth: String,
)