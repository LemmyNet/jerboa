package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonBlockView(
    val person: Person,
    val target: Person,
) : Parcelable
