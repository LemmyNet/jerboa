package com.jerboa.datatypes.types

data class ModRemoveCommunityView(
    var mod_remove_community: ModRemoveCommunity,
    var moderator: Person? = null,
    var community: Community,
)