package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModHideCommunityView(
    val mod_hide_community: ModHideCommunity,
    val admin: Person? = null,
    val community: Community,
) : Parcelable
