package com.jerboa.datatypes.types

data class RemoveComment(
    var comment_id: CommentId,
    var removed: Boolean,
    var reason: String? = null,
    var auth: String,
)