package com.jerboa.datatypes.types

import android.os.Parcelable
import com.jerboa.datatypes.SortType
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPersonDetails(
    val person_id: PersonId? = null,
    val username: String? = null,
    val sort: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" | "TopHour" | "TopSixHour" | "TopTwelveHour" | "TopThreeMonths" | "TopSixMonths" | "TopNineMonths" | "Controversial" | "Scaled" */ = null,
    val page: Int? = null,
    val limit: Int? = null,
    val community_id: CommunityId? = null,
    val saved_only: Boolean? = null,
) : Parcelable
