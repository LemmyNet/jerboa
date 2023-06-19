package com.jerboa.datatypes.types

data class AdminPurgeCommunityView(
    val admin_purge_community: AdminPurgeCommunity,
    val admin: Person? = null,
)
