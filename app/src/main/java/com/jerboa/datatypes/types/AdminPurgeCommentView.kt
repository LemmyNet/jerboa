package com.jerboa.datatypes.types

data class AdminPurgeCommentView(
    var admin_purge_comment: AdminPurgeComment,
    var admin: Person? = null,
    var post: Post,
)