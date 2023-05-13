package com.jerboa.datatypes.types

data class LocalSiteRateLimit(
    var id: Int,
    var local_site_id: LocalSiteId,
    var message: Int,
    var message_per_second: Int,
    var post: Int,
    var post_per_second: Int,
    var register: Int,
    var register_per_second: Int,
    var image: Int,
    var image_per_second: Int,
    var comment: Int,
    var comment_per_second: Int,
    var search: Int,
    var search_per_second: Int,
    var published: String,
    var updated: String? = null,
)