package com.jerboa.datatypes.types

data class GetPersonDetailsResponse(
    var person_view: PersonView,
    var comments: Array<CommentView>,
    var posts: Array<PostView>,
    var moderates: Array<CommunityModeratorView>,
)