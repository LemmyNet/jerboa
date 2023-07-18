package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockPersonResponse(
    val person_view: PersonView,
    val blocked: Boolean,
) : Parcelable
