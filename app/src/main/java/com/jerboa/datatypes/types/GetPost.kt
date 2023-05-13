package com.jerboa.datatypes.types

data class GetPost(
    var id: PostId? = null,
    var comment_id: CommentId? = null,
    var auth: String? = null,
)