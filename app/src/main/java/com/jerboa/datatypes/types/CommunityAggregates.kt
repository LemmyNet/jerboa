package com.jerboa.datatypes.types

data class CommunityAggregates(
    val id: Int,
    val community_id: CommunityId,
    val subscribers: Int,
    val posts: Int,
    val comments: Int,
    val published: String,
    val users_active_day: Int,
    val users_active_week: Int,
    val users_active_month: Int,
    val users_active_half_year: Int,
)
