package com.jerboa.datatypes.types

data class SavePost(
    var post_id: PostId,
    var save: Boolean,
    var auth: String,
)