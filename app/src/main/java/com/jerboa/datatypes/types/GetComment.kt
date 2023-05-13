package com.jerboa.datatypes.types

data class GetComment(
    var id: CommentId,
    var auth: String? = null,
)