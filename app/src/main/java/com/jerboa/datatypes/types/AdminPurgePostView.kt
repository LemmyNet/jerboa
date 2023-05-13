package com.jerboa.datatypes.types

data class AdminPurgePostView(
    var admin_purge_post: AdminPurgePost,
    var admin: Person? = null,
    var community: Community,
)