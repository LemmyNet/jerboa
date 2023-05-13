package com.jerboa.datatypes.types

data class ResolveObjectResponse(
    var comment: CommentView? = null,
    var post: PostView? = null,
    var community: CommunityView? = null,
    var person: PersonView? = null,
)