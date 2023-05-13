package com.jerboa.datatypes.types

data class ModAddCommunityView(
    var mod_add_community: ModAddCommunity,
    var moderator: Person? = null,
    var community: Community,
    var modded_person: Person,
)