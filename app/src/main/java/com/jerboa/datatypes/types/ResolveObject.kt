package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResolveObject(
    val q: String,
    val auth: String,
) : Parcelable
