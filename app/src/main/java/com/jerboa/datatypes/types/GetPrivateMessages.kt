package com.jerboa.datatypes.types

data class GetPrivateMessages(
    val unread_only: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String,
)