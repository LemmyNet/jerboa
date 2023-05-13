package com.jerboa.datatypes.types

data class GetPersonDetails(
    var person_id: PersonId? = null,
    var username: String? = null,
    var sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    var page: Int? = null,
    var limit: Int? = null,
    var community_id: CommunityId? = null,
    var saved_only: Boolean? = null,
    var auth: String? = null,
)