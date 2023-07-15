package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CaptchaResponse(
    val png: String,
    val wav: String,
    val uuid: String,
) : Parcelable
