package com.jerboa.datatypes.types

import android.os.Parcelable
import com.jerboa.datatypes.PostFeatureType
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeaturePost(
    val post_id: PostId,
    val featured: Boolean,
    val feature_type: PostFeatureType /* "Local" | "Community" */,
) : Parcelable
