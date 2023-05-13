package com.jerboa.datatypes.types

data class GetPrivateMessages(
    var unread_only: Boolean? = null,
    var page: Int? = null,
    var limit: Int? = null,
    var auth: String,
)