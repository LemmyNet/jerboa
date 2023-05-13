package com.jerboa.datatypes.types

data class PostReportView(
    var post_report: PostReport,
    var post: Post,
    var community: Community,
    var creator: Person,
    var post_creator: Person,
    var creator_banned_from_community: Boolean,
    var my_vote: Int? = null,
    var counts: PostAggregates,
    var resolver: Person? = null,
)