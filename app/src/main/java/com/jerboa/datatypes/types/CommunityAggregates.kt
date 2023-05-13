package com.jerboa.datatypes.types

data class CommunityAggregates(
    var id: Int,
    var community_id: CommunityId,
    var subscribers: Int,
    var posts: Int,
    var comments: Int,
    var published: String,
    var users_active_day: Int,
    var users_active_week: Int,
    var users_active_month: Int,
    var users_active_half_year: Int,
)