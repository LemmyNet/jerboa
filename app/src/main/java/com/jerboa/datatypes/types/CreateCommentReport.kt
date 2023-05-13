package com.jerboa.datatypes.types

data class CreateCommentReport(
    var comment_id: CommentId,
    var reason: String,
    var auth: String,
)