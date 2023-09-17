package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModBan(
    val id: Int,
    val mod_person_id: PersonId,
    val other_person_id: PersonId,
    val reason: String? = null,
    val banned: Boolean,
    val expires: String? = null,
    val when_: String,
) : Parcelable
