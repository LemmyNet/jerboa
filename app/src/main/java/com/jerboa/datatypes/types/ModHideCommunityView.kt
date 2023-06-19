package com.jerboa.datatypes.types

data class ModHideCommunityView(
    val mod_hide_community: ModHideCommunity,
    val admin: Person? = null,
    val community: Community,
)
