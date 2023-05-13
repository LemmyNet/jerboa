package com.jerboa.datatypes.types

data class Search(
    var q: String,
    var community_id: CommunityId? = null,
    var community_name: String? = null,
    var creator_id: PersonId? = null,
    var type_: SearchType? /* "All" | "Comments" | "Posts" | "Communities" | "Users" | "Url" */ = null,
    var sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    var listing_type: ListingType? /* "All" | "Local" | "Subscribed" */ = null,
    var page: Int? = null,
    var limit: Int? = null,
    var auth: String? = null,
)