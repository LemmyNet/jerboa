package com.jerboa.datatypes.types

data class ListCommunities(
    val type_: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    val sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String? = null,
)
