package com.jerboa.datatypes.types

data class SiteAggregates(
    var id: Int,
    var site_id: SiteId,
    var users: Int,
    var posts: Int,
    var comments: Int,
    var communities: Int,
    var users_active_day: Int,
    var users_active_week: Int,
    var users_active_month: Int,
    var users_active_half_year: Int,
)