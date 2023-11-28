package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginToken(
    val user_id: LocalUserId,
    val published: String,
    val ip: String? = null,
    val user_agent: String? = null,
) : Parcelable
