package com.jerboa.datatypes.types

data class ModRemoveComment(
    var id: Int,
    var mod_person_id: PersonId,
    var comment_id: CommentId,
    var reason: String? = null,
    var removed: Boolean,
    var when_: String,
)