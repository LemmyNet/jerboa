package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityModeratorView(
    val community: Community,
    val moderator: Person,
) : Parcelable
