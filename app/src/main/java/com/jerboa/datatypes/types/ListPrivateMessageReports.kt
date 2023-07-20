package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListPrivateMessageReports(
    val page: Int? = null,
    val limit: Int? = null,
    val unresolved_only: Boolean? = null,
    val auth: String,
) : Parcelable
