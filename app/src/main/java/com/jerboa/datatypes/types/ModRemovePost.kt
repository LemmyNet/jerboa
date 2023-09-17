package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModRemovePost(
    val id: Int,
    val mod_person_id: PersonId,
    val post_id: PostId,
    val reason: String? = null,
    val removed: Boolean,
    val when_: String,
) : Parcelable
