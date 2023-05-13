package com.jerboa.datatypes.types

data class PostView(
    var post: Post,
    var creator: Person,
    var community: Community,
    var creator_banned_from_community: Boolean,
    var counts: PostAggregates,
    var subscribed: SubscribedType /* "Subscribed" | "NotSubscribed" | "Pending" */,
    var saved: Boolean,
    var read: Boolean,
    var creator_blocked: Boolean,
    var my_vote: Int? = null,
    var unread_comments: Int,
)