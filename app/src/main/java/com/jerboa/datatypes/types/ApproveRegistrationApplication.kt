package com.jerboa.datatypes.types

data class ApproveRegistrationApplication(
    var id: Int,
    var approve: Boolean,
    var deny_reason: String? = null,
    var auth: String,
)