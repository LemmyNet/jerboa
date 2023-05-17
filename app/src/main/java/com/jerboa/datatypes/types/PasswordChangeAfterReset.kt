package com.jerboa.datatypes.types

data class PasswordChangeAfterReset(
    val token: String,
    val password: String,
    val password_verify: String,
)
