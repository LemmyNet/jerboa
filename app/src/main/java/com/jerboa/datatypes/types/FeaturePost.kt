package com.jerboa.datatypes.types

data class FeaturePost(
    val post_id: PostId,
    val featured: Boolean,
    val feature_type: PostFeatureType /* "Local" | "Community" */,
    val auth: String,
)
