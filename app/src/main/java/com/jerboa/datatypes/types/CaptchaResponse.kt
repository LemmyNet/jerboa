package com.jerboa.datatypes.types

data class CaptchaResponse(
    val png: String,
    val wav: String,
    val uuid: String,
)
