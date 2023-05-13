package com.jerboa.datatypes.types

data class CreatePostReport(
    var post_id: PostId,
    var reason: String,
    var auth: String,
)