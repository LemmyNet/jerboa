package com.jerboa.datatypes.types

data class Site(
    var id: SiteId,
    var name: String,
    var sidebar: String? = null,
    var published: String,
    var updated: String? = null,
    var icon: String? = null,
    var banner: String? = null,
    var description: String? = null,
    var actor_id: String,
    var last_refreshed_at: String,
    var inbox_url: String,
    var private_key: String? = null,
    var public_key: String,
    var instance_id: InstanceId,
)