package com.jerboa.datatypes.types

data class LocalSiteRateLimit(
    val id: Int,
    val local_site_id: LocalSiteId,
    val message: Int,
    val message_per_second: Int,
    val post: Int,
    val post_per_second: Int,
    val register: Int,
    val register_per_second: Int,
    val image: Int,
    val image_per_second: Int,
    val comment: Int,
    val comment_per_second: Int,
    val search: Int,
    val search_per_second: Int,
    val published: String,
    val updated: String? = null,
)
