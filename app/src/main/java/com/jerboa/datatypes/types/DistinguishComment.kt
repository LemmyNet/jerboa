package com.jerboa.datatypes.types

data class DistinguishComment(
    var comment_id: CommentId,
    var distinguished: Boolean,
    var auth: String,
)