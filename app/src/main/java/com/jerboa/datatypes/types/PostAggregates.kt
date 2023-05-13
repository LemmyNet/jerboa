package com.jerboa.datatypes.types

data class PostAggregates(
    var id: Int,
    var post_id: PostId,
    var comments: Int,
    var score: Int,
    var upvotes: Int,
    var downvotes: Int,
    var published: String,
    var newest_comment_time_necro: String,
    var newest_comment_time: String,
    var featured_community: Boolean,
    var featured_local: Boolean,
)