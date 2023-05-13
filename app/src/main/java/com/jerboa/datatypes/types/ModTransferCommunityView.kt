package com.jerboa.datatypes.types

data class ModTransferCommunityView(
    var mod_transfer_community: ModTransferCommunity,
    var moderator: Person? = null,
    var community: Community,
    var modded_person: Person,
)