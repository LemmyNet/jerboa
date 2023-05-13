package com.jerboa.datatypes.types

data class Login(
    var username_or_email: String,
    var password: String,
    var totp_2fa_token: String? = null,
)