package com.jerboa.datatypes.types

data class GetCommunity(
    var id: CommunityId? = null,
    var name: String? = null,
    var auth: String? = null,
)