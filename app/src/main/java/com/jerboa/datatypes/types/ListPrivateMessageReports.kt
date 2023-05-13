package com.jerboa.datatypes.types

data class ListPrivateMessageReports(
    var page: Int? = null,
    var limit: Int? = null,
    var unresolved_only: Boolean? = null,
    var auth: String,
)