package com.jerboa.datatypes.types

data class ListCommunities(
    var type_: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    var sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    var page: Int? = null,
    var limit: Int? = null,
    var auth: String? = null,
)