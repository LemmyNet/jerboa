package com.jerboa.datatypes.types

data class DeleteComment(
    var comment_id: CommentId,
    var deleted: Boolean,
    var auth: String,
)