package com.jerboa.datatypes.types

data class LoginResponse(
    var jwt: String? = null,
    var registration_created: Boolean,
    var verify_email_sent: Boolean,
)