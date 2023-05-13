package com.jerboa.datatypes.types

data class EditCommunity(
    var community_id: CommunityId,
    var title: String? = null,
    var description: String? = null,
    var icon: String? = null,
    var banner: String? = null,
    var nsfw: Boolean? = null,
    var posting_restricted_to_mods: Boolean? = null,
    var discussion_languages: Array<LanguageId>? = null,
    var auth: String,
)