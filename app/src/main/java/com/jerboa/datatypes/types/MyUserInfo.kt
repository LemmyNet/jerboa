package com.jerboa.datatypes.types

data class MyUserInfo(
    var local_user_view: LocalUserView,
    var follows: Array<CommunityFollowerView>,
    var moderates: Array<CommunityModeratorView>,
    var community_blocks: Array<CommunityBlockView>,
    var person_blocks: Array<PersonBlockView>,
    var discussion_languages: Array<LanguageId>,
)