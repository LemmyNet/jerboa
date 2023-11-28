package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateTotp(
    val totp_token: String,
    val enabled: Boolean,
) : Parcelable
