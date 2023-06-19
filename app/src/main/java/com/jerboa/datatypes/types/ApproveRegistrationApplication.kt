package com.jerboa.datatypes.types

data class ApproveRegistrationApplication(
    val id: Int,
    val approve: Boolean,
    val deny_reason: String? = null,
    val auth: String,
)
