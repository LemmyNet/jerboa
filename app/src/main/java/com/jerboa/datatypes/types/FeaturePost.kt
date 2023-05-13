package com.jerboa.datatypes.types

data class FeaturePost(
    var post_id: PostId,
    var featured: Boolean,
    var feature_type: PostFeatureType /* "Local" | "Community" */,
    var auth: String,
)