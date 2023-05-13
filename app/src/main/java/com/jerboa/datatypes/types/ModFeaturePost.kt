package com.jerboa.datatypes.types

data class ModFeaturePost(
    var id: Int,
    var mod_person_id: PersonId,
    var post_id: PostId,
    var featured: Boolean,
    var when_: String,
    var is_featured_community: Boolean,
)