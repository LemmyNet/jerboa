package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddAdmin(
    val person_id: PersonId,
    val added: Boolean,
    val auth: String,
) : Parcelable
