package com.jerboa.datatypes.types

data class CreatePostLike(
    var post_id: PostId,
    var score: Int,
    var auth: String,
)