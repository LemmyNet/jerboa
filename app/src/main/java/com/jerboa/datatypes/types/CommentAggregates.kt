package com.jerboa.datatypes.types

data class CommentAggregates(
    var id: Int,
    var comment_id: CommentId,
    var score: Int,
    var upvotes: Int,
    var downvotes: Int,
    var published: String,
    var child_count: Int,
)