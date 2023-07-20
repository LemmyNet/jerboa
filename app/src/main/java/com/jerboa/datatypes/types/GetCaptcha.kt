package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetCaptcha(
    val auth: String? = null,
) : Parcelable
