package com.jerboa.datatypes.types

data class GetPersonDetails(
    val person_id: PersonId? = null,
    val username: String? = null,
    val sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val community_id: CommunityId? = null,
    val saved_only: Boolean? = null,
    val auth: String? = null,
)
