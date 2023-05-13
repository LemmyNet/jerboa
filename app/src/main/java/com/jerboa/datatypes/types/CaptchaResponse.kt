package com.jerboa.datatypes.types

data class CaptchaResponse(
    var png: String,
    var wav: String,
    var uuid: String,
)