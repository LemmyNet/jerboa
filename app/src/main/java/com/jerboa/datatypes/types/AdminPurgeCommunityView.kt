package com.jerboa.datatypes.types

data class AdminPurgeCommunityView(
    var admin_purge_community: AdminPurgeCommunity,
    var admin: Person? = null,
)