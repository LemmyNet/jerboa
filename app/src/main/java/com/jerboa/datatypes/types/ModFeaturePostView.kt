package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModFeaturePostView(
    val mod_feature_post: ModFeaturePost,
    val moderator: Person? = null,
    val post: Post,
    val community: Community,
) : Parcelable
