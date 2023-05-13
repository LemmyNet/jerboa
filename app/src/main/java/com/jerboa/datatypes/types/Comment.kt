package com.jerboa.datatypes.types

data class Comment(
    var id: CommentId,
    var creator_id: PersonId,
    var post_id: PostId,
    var content: String,
    var removed: Boolean,
    var published: String,
    var updated: String? = null,
    var deleted: Boolean,
    var ap_id: String,
    var local: Boolean,
    var path: String,
    var distinguished: Boolean,
    var language_id: LanguageId,
)