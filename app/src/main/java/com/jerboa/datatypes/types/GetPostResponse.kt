package com.jerboa.datatypes.types

data class GetPostResponse(
    var post_view: PostView,
    var community_view: CommunityView,
    var moderators: Array<CommunityModeratorView>,
    var cross_posts: Array<PostView>,
    var online: Int,
)