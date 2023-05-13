package com.jerboa.datatypes.types

data class ModHideCommunityView(
    var mod_hide_community: ModHideCommunity,
    var admin: Person? = null,
    var community: Community,
)