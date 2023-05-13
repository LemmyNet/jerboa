package com.jerboa.datatypes.types

data class GetPersonMentions(
    var sort: CommentSortType? /* "Hot" | "Top" | "New" | "Old" */ = null,
    var page: Int? = null,
    var limit: Int? = null,
    var unread_only: Boolean? = null,
    var auth: String,
)