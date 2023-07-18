package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ListRegistrationApplicationsResponse(
    val registration_applications: List<RegistrationApplicationView>,
) : Parcelable
