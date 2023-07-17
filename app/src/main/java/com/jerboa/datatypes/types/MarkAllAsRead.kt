package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkAllAsRead(
    val auth: String,
) : Parcelable
