package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BanFromCommunityResponse(
    val person_view: PersonView,
    val banned: Boolean,
) : Parcelable
