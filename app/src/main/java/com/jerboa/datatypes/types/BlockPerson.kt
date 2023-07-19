package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockPerson(
    val person_id: PersonId,
    val block: Boolean,
    val auth: String,
) : Parcelable
