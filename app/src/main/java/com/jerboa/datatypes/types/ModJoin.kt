package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModJoin(
    val community_id: CommunityId,
) : Parcelable
