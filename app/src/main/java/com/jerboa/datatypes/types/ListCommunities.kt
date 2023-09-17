package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListCommunities(
    val type_: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    val sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" | "TopHour" | "TopSixHour" | "TopTwelveHour" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String? = null,
) : Parcelable
