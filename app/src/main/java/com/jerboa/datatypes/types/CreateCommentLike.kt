package com.jerboa.datatypes.types

data class CreateCommentLike(
    var comment_id: CommentId,
    var score: Int,
    var auth: String,
)