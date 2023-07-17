package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModRemovePostView(
    val mod_remove_post: ModRemovePost,
    val moderator: Person? = null,
    val post: Post,
    val community: Community,
) : Parcelable
