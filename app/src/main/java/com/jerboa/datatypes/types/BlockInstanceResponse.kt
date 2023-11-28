package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockInstanceResponse(
    val blocked: Boolean,
) : Parcelable
