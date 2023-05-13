package com.jerboa.datatypes.types

data class CommunityResponse(
    var community_view: CommunityView,
    var discussion_languages: Array<LanguageId>,
)