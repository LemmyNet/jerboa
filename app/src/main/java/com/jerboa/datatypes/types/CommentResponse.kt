package com.jerboa.datatypes.types

data class CommentResponse(
    var comment_view: CommentView,
    var recipient_ids: Array<LocalUserId>,
    var form_id: String? = null,
)