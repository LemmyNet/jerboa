package com.jerboa.datatypes.types

data class ModRemoveCommunityView(
    val mod_remove_community: ModRemoveCommunity,
    val moderator: Person? = null,
    val community: Community,
)
