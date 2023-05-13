package com.jerboa.datatypes.types

data class SearchResponse(
    var type_: SearchType /* "All" | "Comments" | "Posts" | "Communities" | "Users" | "Url" */,
    var comments: Array<CommentView>,
    var posts: Array<PostView>,
    var communities: Array<CommunityView>,
    var users: Array<PersonView>,
)