package com.jerboa.datatypes.types

data class Tagline(
    var id: Int,
    var local_site_id: LocalSiteId,
    var content: String,
    var published: String,
    var updated: String? = null,
)