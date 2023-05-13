package com.jerboa.datatypes.types

data class ModBanFromCommunityView(
    var mod_ban_from_community: ModBanFromCommunity,
    var moderator: Person? = null,
    var community: Community,
    var banned_person: Person,
)