package com.jerboa.datatypes.types

data class ChangePassword(
    val new_password: String,
    val new_password_verify: String,
    val old_password: String,
    val auth: String,
)
