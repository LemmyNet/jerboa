package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegistrationApplicationResponse(
    val registration_application: RegistrationApplicationView,
) : Parcelable
