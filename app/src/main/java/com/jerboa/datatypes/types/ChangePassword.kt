package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangePassword(
    val new_password: String,
    val new_password_verify: String,
    val old_password: String,
) : Parcelable
