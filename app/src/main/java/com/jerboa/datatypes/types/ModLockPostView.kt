package com.jerboa.datatypes.types

data class ModLockPostView(
    var mod_lock_post: ModLockPost,
    var moderator: Person? = null,
    var post: Post,
    var community: Community,
)