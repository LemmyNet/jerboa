package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockCommunityResponse(
    val community_view: CommunityView,
    val blocked: Boolean,
) : Parcelable
