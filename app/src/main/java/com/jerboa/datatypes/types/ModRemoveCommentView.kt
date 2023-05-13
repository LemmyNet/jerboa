package com.jerboa.datatypes.types

data class ModRemoveCommentView(
    var mod_remove_comment: ModRemoveComment,
    var moderator: Person? = null,
    var comment: Comment,
    var commenter: Person,
    var post: Post,
    var community: Community,
)