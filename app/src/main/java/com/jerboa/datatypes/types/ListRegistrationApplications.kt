package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListRegistrationApplications(
    val unread_only: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
) : Parcelable
