package com.jerboa.datatypes.types

data class ModRemovePostView(
    var mod_remove_post: ModRemovePost,
    var moderator: Person? = null,
    var post: Post,
    var community: Community,
)