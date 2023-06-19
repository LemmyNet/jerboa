package com.jerboa.datatypes.types

data class ModFeaturePost(
    val id: Int,
    val mod_person_id: PersonId,
    val post_id: PostId,
    val featured: Boolean,
    val when_: String,
    val is_featured_community: Boolean,
)
