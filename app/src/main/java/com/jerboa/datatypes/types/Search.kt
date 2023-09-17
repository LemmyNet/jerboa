package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Search(
    val q: String,
    val community_id: CommunityId? = null,
    val community_name: String? = null,
    val creator_id: PersonId? = null,
    val type_: SearchType? /* "All" | "Comments" | "Posts" | "Communities" | "Users" | "Url" */ = null,
    val sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" | "TopHour" | "TopSixHour" | "TopTwelveHour" */ = null,
    val listing_type: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String? = null,
) : Parcelable
