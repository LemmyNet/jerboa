package com.jerboa.datatypes.types

data class SaveComment(
    var comment_id: CommentId,
    var save: Boolean,
    var auth: String,
)