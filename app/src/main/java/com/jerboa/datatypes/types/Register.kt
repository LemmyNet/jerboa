package com.jerboa.datatypes.types

data class Register(
    var username: String,
    var password: String,
    var password_verify: String,
    var show_nsfw: Boolean,
    var email: String? = null,
    var captcha_uuid: String? = null,
    var captcha_answer: String? = null,
    var honeypot: String? = null,
    var answer: String? = null,
)