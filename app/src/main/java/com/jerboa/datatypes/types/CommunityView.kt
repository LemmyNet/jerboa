package com.jerboa.datatypes.types

data class CommunityView(
    var community: Community,
    var subscribed: SubscribedType /* "Subscribed" | "NotSubscribed" | "Pending" */,
    var blocked: Boolean,
    var counts: CommunityAggregates,
)