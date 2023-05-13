package com.jerboa.datatypes.types

data class ChangePassword(
    var new_password: String,
    var new_password_verify: String,
    var old_password: String,
    var auth: String,
)