package com.jerboa.datatypes.types

data class PurgeComment(
    var comment_id: CommentId,
    var reason: String? = null,
    var auth: String,
)