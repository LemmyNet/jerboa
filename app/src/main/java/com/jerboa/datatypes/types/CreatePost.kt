package com.jerboa.datatypes.types

data class CreatePost(
    var name: String,
    var community_id: CommunityId,
    var url: String? = null,
    var body: String? = null,
    var honeypot: String? = null,
    var nsfw: Boolean? = null,
    var language_id: LanguageId? = null,
    var auth: String,
)