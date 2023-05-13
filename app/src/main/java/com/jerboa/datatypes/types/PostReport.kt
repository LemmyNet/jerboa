package com.jerboa.datatypes.types

data class PostReport(
    var id: PostReportId,
    var creator_id: PersonId,
    var post_id: PostId,
    var original_post_name: String,
    var original_post_url: String? = null,
    var original_post_body: String? = null,
    var reason: String,
    var resolved: Boolean,
    var resolver_id: PersonId? = null,
    var published: String,
    var updated: String? = null,
)