package com.jerboa.datatypes.types

data class GetComments(
    var type_: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    var sort: CommentSortType? /* "Hot" | "Top" | "New" | "Old" */ = null,
    var max_depth: Int? = null,
    var page: Int? = null,
    var limit: Int? = null,
    var community_id: CommunityId? = null,
    var community_name: String? = null,
    var post_id: PostId? = null,
    var parent_id: CommentId? = null,
    var saved_only: Boolean? = null,
    var auth: String? = null,
)