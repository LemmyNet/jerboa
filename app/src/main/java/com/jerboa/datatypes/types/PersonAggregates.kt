package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonAggregates(
    val person_id: PersonId,
    val post_count: Int,
    val comment_count: Int,
) : Parcelable
