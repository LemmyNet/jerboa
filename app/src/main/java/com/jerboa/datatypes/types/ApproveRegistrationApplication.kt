package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApproveRegistrationApplication(
    val id: Int,
    val approve: Boolean,
    val deny_reason: String? = null,
) : Parcelable
