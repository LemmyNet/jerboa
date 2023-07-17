package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PasswordChangeAfterReset(
    val token: String,
    val password: String,
    val password_verify: String,
) : Parcelable
