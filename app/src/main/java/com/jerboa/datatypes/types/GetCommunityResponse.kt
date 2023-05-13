package com.jerboa.datatypes.types

data class GetCommunityResponse(
    var community_view: CommunityView,
    var site: Site? = null,
    var moderators: Array<CommunityModeratorView>,
    var online: Int,
    var discussion_languages: Array<LanguageId>,
    var default_post_language: LanguageId? = null,
)