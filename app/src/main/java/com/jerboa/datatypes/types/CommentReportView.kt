package com.jerboa.datatypes.types

data class CommentReportView(
    var comment_report: CommentReport,
    var comment: Comment,
    var post: Post,
    var community: Community,
    var creator: Person,
    var comment_creator: Person,
    var counts: CommentAggregates,
    var creator_banned_from_community: Boolean,
    var my_vote: Int? = null,
    var resolver: Person? = null,
)