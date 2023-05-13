package com.jerboa.datatypes.types

data class ModFeaturePostView(
    var mod_feature_post: ModFeaturePost,
    var moderator: Person? = null,
    var post: Post,
    var community: Community,
)