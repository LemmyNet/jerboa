package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenerateTotpSecretResponse(
    val totp_secret_url: String,
) : Parcelable
