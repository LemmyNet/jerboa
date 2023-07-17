package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModAdd(
    val id: Int,
    val mod_person_id: PersonId,
    val other_person_id: PersonId,
    val removed: Boolean,
    val when_: String,
) : Parcelable
