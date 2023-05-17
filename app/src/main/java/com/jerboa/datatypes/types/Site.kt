package com.jerboa.datatypes.types

data class Site(
    val id: SiteId,
    val name: String,
    val sidebar: String? = null,
    val published: String,
    val updated: String? = null,
    val icon: String? = null,
    val banner: String? = null,
    val description: String? = null,
    val actor_id: String,
    val last_refreshed_at: String,
    val inbox_url: String,
    val private_key: String? = null,
    val public_key: String,
    val instance_id: InstanceId,
)
