package com.jerboa.datatypes.types

data class CreateCommunity(
    var name: String,
    var title: String,
    var description: String? = null,
    var icon: String? = null,
    var banner: String? = null,
    var nsfw: Boolean? = null,
    var posting_restricted_to_mods: Boolean? = null,
    var discussion_languages: Array<LanguageId>? = null,
    var auth: String,
)