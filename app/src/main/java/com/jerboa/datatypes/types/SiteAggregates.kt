package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SiteAggregates(
    val site_id: SiteId,
    val users: Int,
    val posts: Int,
    val comments: Int,
    val communities: Int,
    val users_active_day: Int,
    val users_active_week: Int,
    val users_active_month: Int,
    val users_active_half_year: Int,
) : Parcelable
