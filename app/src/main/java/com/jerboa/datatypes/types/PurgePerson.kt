package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurgePerson(
    val person_id: PersonId,
    val reason: String? = null,
    val auth: String,
) : Parcelable
