package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUnreadRegistrationApplicationCountResponse(
    val registration_applications: Int,
) : Parcelable
