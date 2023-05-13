package com.jerboa.datatypes.types

data class EditPost(
    var post_id: PostId,
    var name: String? = null,
    var url: String? = null,
    var body: String? = null,
    var nsfw: Boolean? = null,
    var language_id: LanguageId? = null,
    var auth: String,
)