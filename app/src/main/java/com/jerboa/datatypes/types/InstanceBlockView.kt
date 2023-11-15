package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstanceBlockView(
    val person: Person,
    val instance: Instance,
    val site: Site? = null,
) : Parcelable
