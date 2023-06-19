package com.jerboa.datatypes.types

data class GetCommunityResponse(
    val community_view: CommunityView,
    val site: Site? = null,
    val moderators: List<CommunityModeratorView>,
    val online: Int,
    val discussion_languages: List<LanguageId>,
    val default_post_language: LanguageId? = null,
)
