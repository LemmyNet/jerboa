package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonView(
    val person: Person,
    val counts: PersonAggregates,
) : Parcelable
