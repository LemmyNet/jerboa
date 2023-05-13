package com.jerboa.datatypes.types

data class CreateComment(
    var content: String,
    var post_id: PostId,
    var parent_id: CommentId? = null,
    var language_id: LanguageId? = null,
    var form_id: String? = null,
    var auth: String,
)