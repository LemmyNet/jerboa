package com.jerboa.datatypes.types

data class GetPosts(
    var type_: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    var sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    var page: Int? = null,
    var limit: Int? = null,
    var community_id: CommunityId? = null,
    var community_name: String? = null,
    var saved_only: Boolean? = null,
    var auth: String? = null,
)