package com.jerboa.datatypes.types

data class CommentReport(
    var id: CommentReportId,
    var creator_id: PersonId,
    var comment_id: CommentId,
    var original_comment_text: String,
    var reason: String,
    var resolved: Boolean,
    var resolver_id: PersonId? = null,
    var published: String,
    var updated: String? = null,
)