package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Login(
    val username_or_email: String,
    val password: String,
    val totp_2fa_token: String? = null,
) : Parcelable
