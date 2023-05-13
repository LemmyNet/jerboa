package com.jerboa.datatypes.types

data class EditComment(
    var comment_id: CommentId,
    var content: String? = null,
    var language_id: LanguageId? = null,
    var form_id: String? = null,
    var auth: String,
)