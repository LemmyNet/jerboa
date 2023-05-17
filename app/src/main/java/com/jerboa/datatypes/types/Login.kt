package com.jerboa.datatypes.types

data class Login(
    val username_or_email: String,
    val password: String,
    val totp_2fa_token: String? = null,
)
