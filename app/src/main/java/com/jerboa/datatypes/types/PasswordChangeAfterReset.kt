package com.jerboa.datatypes.types

data class PasswordChangeAfterReset(
    var token: String,
    var password: String,
    var password_verify: String,
)