package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModRemoveCommunity(
    val id: Int,
    val mod_person_id: PersonId,
    val community_id: CommunityId,
    val reason: String? = null,
    val removed: Boolean,
    val expires: String? = null,
    val when_: String,
) : Parcelable
